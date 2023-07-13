package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.core.ServerLocations;
import gbw.riot.tftfieldanalysis.core.SummonerDTO;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.JSONWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class DataRetrievalService {

     @FunctionalInterface
     public interface MatchParser{
         /**
          * A match parser function should return true to end the process early.
          * @param data
          * @return wether or not to immediatly end the process.
          */
         boolean apply(MatchData data);
     }
     @Autowired
     private SecretsService secrets;

    private final RestTemplate getMatchDataTemplate, getMatchIdsTemplate, getPUUIDTemplate;

    public DataRetrievalService(RestTemplateBuilder restTemplateBuilder) {
        this.getMatchDataTemplate = restTemplateBuilder.build();
        this.getMatchIdsTemplate = restTemplateBuilder.build();
        this.getPUUIDTemplate = restTemplateBuilder.build();
    }

    private ValueErrorTuple<HttpEntity<?>,Exception> getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        ValueErrorTuple<String,Exception> errVal = secrets.getByKey("X-Riot-Token");
        headers.set("X-Riot-Token", errVal.value());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ValueErrorTuple.of(new HttpEntity<>("",headers), errVal.error());
    }

    public ValueErrorTuple<MatchData,Exception> getMatchData(String matchId) {
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/"+matchId;
        ValueErrorTuple<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValueErrorTuple.error(headers.error());
        }
        ValueErrorTuple<ResponseEntity<MatchData>, RestClientException>
                requestAttempt = ValueErrorTuple.encapsulate(
                        () -> getMatchDataTemplate.exchange(url, HttpMethod.GET, headers.value(), MatchData.class)
        );
        if(requestAttempt.error() != null){
            return ValueErrorTuple.error(requestAttempt.error());
        }
        ResponseEntity<MatchData> response = requestAttempt.value();
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValueErrorTuple.error(new Exception(response.toString()));
        }
        return ValueErrorTuple.value(response.getBody());
    }

    public ValueErrorTuple<String[],Exception> getMatchIdsForPlayer(String playerPUUID){
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/"+playerPUUID+"/ids?start=0&count=20";
        ValueErrorTuple<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValueErrorTuple.error(headers.error());
        }
        ValueErrorTuple<ResponseEntity<String>, RestClientException>
                responseAttempt = ValueErrorTuple.encapsulate(
                        () -> getMatchIdsTemplate.exchange(url, HttpMethod.GET, headers.value(), String.class)
        );
        if(responseAttempt.error() != null){
            return ValueErrorTuple.error(responseAttempt.error());
        }
        ResponseEntity<String> response = responseAttempt.value();

        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValueErrorTuple.error(new Exception(response.toString()));
        }
        String body = response.getBody();
        return ValueErrorTuple.value(
                body == null ? new String[0] : JSONWrapper.parseValueArray(body)
        );
    }

    public ValueErrorTuple<SummonerDTO, Exception> getAccount(String IGN, ServerLocations target){
        String url = "https://"+target.domain+".api.riotgames.com/tft/summoner/v1/summoners/by-name/"+IGN;
        ValueErrorTuple<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValueErrorTuple.error(headers.error());
        }
        ValueErrorTuple<ResponseEntity<SummonerDTO>, RestClientException>
                responseAttempt = ValueErrorTuple.encapsulate(
                () -> getPUUIDTemplate.exchange(url, HttpMethod.GET, headers.value(), SummonerDTO.class)
        );
        if(responseAttempt.error() != null){
            return ValueErrorTuple.error(responseAttempt.error());
        }
        ResponseEntity<SummonerDTO> response = responseAttempt.value();
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValueErrorTuple.error(new Exception(response.toString()));
        }
        return ValueErrorTuple.value(response.getBody());
    }

    public ValueErrorTuple<Set<String>,Exception> start(ModelTrainingService.TrainingConfiguration config, String basePlayerPUUID, MatchParser forEachMatch, Set<String> excludedMatches){
        HashSet<String> playersParsed = new HashSet<>();
        MatchData[] dataPool = new MatchData[config.maxMatchCount];
        Thread[] threadPool = new Thread[config.maxMatchCount];
        //Algorithm structure:
        //  first, retrieve all match ids for players
        //  second, spin out to load the data for each match id

        if(excludedMatches == null){
            return ValueErrorTuple.error(new IllegalArgumentException("Excluded match set must not be null."));
        }
        HashSet<String> matchesParsed = new HashSet<>(excludedMatches);
        int matchesParsedCount = 0;
        Queue<String> playersYetToParse = new LinkedBlockingQueue<>(Collections.singleton(basePlayerPUUID));

        while(matchesParsedCount < config.maxMatchCount && !playersYetToParse.isEmpty()){
            String player = playersYetToParse.poll();
            if(player == null){
                break;
            }

            ValueErrorTuple<String[],Exception> matchIdsRequest = getMatchIdsForPlayer(player);
            if(matchIdsRequest.error() != null){
                return ValueErrorTuple.of(matchesParsed, new Exception(matchIdsRequest.error()));
            }
            String[] matchIds = ArrayUtil.resizeStringArray(matchIdsRequest.value(), match -> !matchesParsed.contains(match));

            for(String id : matchIds){
                ValueErrorTuple<MatchData,Exception> matchRequest = getMatchData(id);
                if(matchRequest.error() != null){
                    return ValueErrorTuple.of(matchesParsed, matchRequest.error());
                }
                boolean abort = forEachMatch.apply(matchRequest.value());
                matchesParsed.add(id);
                matchesParsedCount++;
                if(abort) return ValueErrorTuple.value(matchesParsed);

                //collection difference
                List<String> possibleNewPlayers = matchRequest.value().metadata().participants();
                String[] uniqueNewPlayers = ArrayUtil.resizeStringArray(
                        possibleNewPlayers.toArray(new String[0]),
                        p -> !playersParsed.contains(p)
                );
                playersYetToParse.addAll(List.of(uniqueNewPlayers));
            }

            playersParsed.add(player);
        }

        if(matchesParsedCount < config.maxMatchCount){
            return ValueErrorTuple.of(
                    matchesParsed,
                    new RuntimeException("Unable to reach desired match count: " + matchesParsedCount + " / " + config.maxMatchCount)
            );
        }

        return ValueErrorTuple.value(matchesParsed);
    }

    //Retrieve MatchData:
    // https://europe.api.riotgames.com/tft/match/v1/matches/EUW1_6483434061

    //Retrieve match ids for player:
    // https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/<PLAYER PUUID>/ids?start=0&count=20
    //my puuid: PbehKkjRrNApiTrB_Q5IH5a0EAozAHNRFdd_ObZQW1c4Pt3ZL22A-gt1kFPOaxpERXRCPSQWpy7kNQ
    /*
        Known match ids:
            "EUW1_6484922710",
            "EUW1_6484891521",
            "EUW1_6484852567",
            "EUW1_6484808791",
            "EUW1_6484043923",
            "EUW1_6484018073",
            "EUW1_6483983936",
            "EUW1_6483669411",
            "EUW1_6483672546",
            "EUW1_6483655780",
            "EUW1_6483643988",
            "EUW1_6483630304",
            "EUW1_6483604203",
            "EUW1_6483583970",
            "EUW1_6483564136",
            "EUW1_6483466336",
            "EUW1_6483434061",
            "EUW1_6483398986",
            "EUW1_6483381754",
            "EUW1_6483318216"
     */

}
