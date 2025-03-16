package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.core.ServerLocations;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.SummonerDTO;
import gbw.riot.tftfieldanalysis.core.ValErr;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.JSONWrapper;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.AccountDTO;
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

    private ValErr<HttpEntity<?>,Exception> getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        ValErr<String,Exception> errVal = secrets.getSecret("X-Riot-Token");
        headers.set("X-Riot-Token", errVal.value());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ValErr.of(new HttpEntity<>("",headers), errVal.error());
    }

    public ValErr<MatchData,Exception> getMatchData(String matchId) {
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/"+matchId;
        ValErr<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValErr.error(headers.error());
        }
        ValErr<ResponseEntity<MatchData>, RestClientException>
                requestAttempt = ValErr.encapsulate(
                        () -> getMatchDataTemplate.exchange(url, HttpMethod.GET, headers.value(), MatchData.class)
        );
        if(requestAttempt.error() != null){
            return ValErr.error(requestAttempt.error());
        }
        ResponseEntity<MatchData> response = requestAttempt.value();
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValErr.error(new Exception(response.toString()));
        }
        return ValErr.value(response.getBody());
    }

    public ValErr<String[],Exception> getMatchIdsForPlayer(String playerPUUID){
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/"+playerPUUID+"/ids?start=0&count=20";
        ValErr<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValErr.error(headers.error());
        }
        ValErr<ResponseEntity<String>, RestClientException>
                responseAttempt = ValErr.encapsulate(
                        () -> getMatchIdsTemplate.exchange(url, HttpMethod.GET, headers.value(), String.class)
        );
        if(responseAttempt.error() != null){
            return ValErr.error(responseAttempt.error());
        }
        ResponseEntity<String> response = responseAttempt.value();

        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValErr.error(new Exception(response.toString()));
        }
        String body = response.getBody();
        return ValErr.value(
                body == null ? new String[0] : JSONWrapper.parseValueArray(body)
        );
    }

    public ValErr<AccountDTO, Exception> getAccount(String IGN, ServerLocations target, String tagLine) {
        final String accountUrl = "https://"+target.continent +".api.riotgames.com/riot/account/v1/accounts/by-riot-id/"+IGN+"/"+tagLine;
        ValErr<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValErr.error(headers.error());
        }
        ValErr<ResponseEntity<AccountDTO>, RestClientException>
                accountResponseAttempt = ValErr.encapsulate(
                () -> getPUUIDTemplate.exchange(accountUrl, HttpMethod.GET, headers.value(), AccountDTO.class)
        );
        if(accountResponseAttempt.error() != null){
            return ValErr.error(accountResponseAttempt.error());
        }
        return ValErr.value(accountResponseAttempt.value().getBody());
    }

    public ValErr<SummonerDTO, Exception> getSummoner(String IGN, ServerLocations target, String tagLine){
        final ValErr<AccountDTO, Exception> accountAttempt = getAccount(IGN, target, tagLine);
        if(accountAttempt.error() != null){
            return ValErr.error(accountAttempt.error());
        }
        ValErr<HttpEntity<?>,Exception> headers = getHeaders();
        final String summonerUrl = "https://"+target.domain +".api.riotgames.com/tft/summoner/v1/summoners/by-puuid/"+accountAttempt.value().puuid();
        ValErr<ResponseEntity<SummonerDTO>, RestClientException>
                summonerResponseAttempt = ValErr.encapsulate(
                () -> getPUUIDTemplate.exchange(summonerUrl, HttpMethod.GET, headers.value(), SummonerDTO.class)
        );

        ResponseEntity<SummonerDTO> response = summonerResponseAttempt.value();
        if(response.getStatusCode() == HttpStatusCode.valueOf(404)){
            return ValErr.error(new Exception("No Such Summoner"));
        }
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValErr.error(new Exception(response.toString()));
        }
        return ValErr.value(response.getBody());
    }

    public ValErr<Set<String>,Exception> start(ModelTrainingService.TrainingConfiguration config, String basePlayerPUUID, MatchParser forEachMatch, Set<String> excludedMatches){
        HashSet<String> playersParsed = new HashSet<>();
        //Algorithm structure:
        //  first, retrieve all match ids for players
        //  second, spin out to load the data for each match id

        if(excludedMatches == null){
            return ValErr.error(new IllegalArgumentException("Excluded match set must not be null."));
        }
        HashSet<String> matchesParsed = new HashSet<>(excludedMatches);
        int matchesParsedCount = 0;
        Queue<String> playersYetToParse = new LinkedBlockingQueue<>(Collections.singleton(basePlayerPUUID));

        while(matchesParsedCount < config.maxMatchCount && !playersYetToParse.isEmpty()){
            String player = playersYetToParse.poll();
            if(player == null){
                break;
            }

            ValErr<String[],Exception> matchIdsRequest = getMatchIdsForPlayer(player);
            if(matchIdsRequest.error() != null){
                return ValErr.of(matchesParsed, new Exception(matchIdsRequest.error()));
            }
            String[] matchIds = ArrayUtil.resizeStringArray(matchIdsRequest.value(), match -> !matchesParsed.contains(match));

            for(String id : matchIds){
                ValErr<MatchData,Exception> matchRequest = getMatchData(id);
                if(matchRequest.error() != null){
                    return ValErr.of(matchesParsed, matchRequest.error());
                }
                boolean abort = forEachMatch.apply(matchRequest.value());
                matchesParsed.add(id);
                matchesParsedCount++;
                if(abort) return ValErr.value(matchesParsed);

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
            return ValErr.of(
                    matchesParsed,
                    new RuntimeException("Unable to reach desired match count: " + matchesParsedCount + " / " + config.maxMatchCount)
            );
        }

        return ValErr.value(matchesParsed);
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
