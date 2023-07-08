package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.core.ValueErrorTouple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.JSONWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

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

    private final RestTemplate getMatchDataTemplate, getMatchIdsTemplate;
    private final String basePlayer = "PbehKkjRrNApiTrB_Q5IH5a0EAozAHNRFdd_ObZQW1c4Pt3ZL22A-gt1kFPOaxpERXRCPSQWpy7kNQ";

    public DataRetrievalService(RestTemplateBuilder restTemplateBuilder) {
        this.getMatchDataTemplate = restTemplateBuilder.build();
        this.getMatchIdsTemplate = restTemplateBuilder.build();
    }

    private ValueErrorTouple<HttpEntity<?>,Exception> getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        ValueErrorTouple<String,Exception> errVal = secrets.getByKey("X-Riot-Token");
        headers.set("X-Riot-Token", errVal.value());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ValueErrorTouple.of(new HttpEntity<>("",headers), errVal.error());
    }

    public ValueErrorTouple<MatchData,Exception> getMatchData(String matchId) {
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/"+matchId;
        ValueErrorTouple<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValueErrorTouple.error(headers.error());
        }
        ResponseEntity<MatchData> response = getMatchDataTemplate.exchange(url, HttpMethod.GET, headers.value(), MatchData.class);
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValueErrorTouple.error(new Exception(response.toString()));
        }
        return ValueErrorTouple.value(response.getBody());
    }

    public ValueErrorTouple<String[],Exception> getMatchIdsForPlayer(String playerPUUID){
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/"+playerPUUID+"/ids?start=0&count=20";
        ValueErrorTouple<HttpEntity<?>,Exception> headers = getHeaders();
        if(headers.error() != null){
            return ValueErrorTouple.error(headers.error());
        }
        ResponseEntity<String> response = getMatchIdsTemplate.exchange(url, HttpMethod.GET, headers.value(), String.class);
        if(response.getStatusCode() != HttpStatusCode.valueOf(200)){
            return ValueErrorTouple.error(new Exception(response.toString()));
        }
        String body = response.getBody();
        return ValueErrorTouple.value(
                body == null ? new String[0] : JSONWrapper.parseValueArray(body)
        );
    }

    public ValueErrorTouple<Set<String>,Exception> run(int maxCount, MatchParser forEachMatch, Set<String> excludedMatches){
        HashSet<String> playersParsed = new HashSet<>();

        if(excludedMatches == null){
            return ValueErrorTouple.error(new IllegalArgumentException("Excluded match set must not be null."));
        }
        HashSet<String> matchesParsed = new HashSet<>(excludedMatches);
        int matchesParsedCount = 0;
        Queue<String> playersYetToParse = new LinkedBlockingQueue<>(Collections.singleton(basePlayer));

        while(matchesParsedCount < maxCount && !playersYetToParse.isEmpty()){
            String player = playersYetToParse.poll();
            if(player == null){
                break;
            }

            ValueErrorTouple<String[],Exception> matchIdsRequest = getMatchIdsForPlayer(player);
            if(matchIdsRequest.error() != null){
                return ValueErrorTouple.of(matchesParsed, new Exception(matchIdsRequest.error()));
            }
            String[] matchIds = ArrayUtil.resizeStringArray(matchIdsRequest.value(), match -> !matchesParsed.contains(match));

            for(String id : matchIds){
                ValueErrorTouple<MatchData,Exception> matchRequest = getMatchData(id);
                if(matchRequest.error() != null){
                    return ValueErrorTouple.of(matchesParsed, matchRequest.error());
                }
                boolean abort = forEachMatch.apply(matchRequest.value());
                matchesParsed.add(id);
                matchesParsedCount++;
                if(abort) return ValueErrorTouple.value(matchesParsed);

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
        return ValueErrorTouple.value(matchesParsed);
    }

    //Retrieve MatchData:
    // https://europe.api.riotgames.com/tft/match/v1/matches/EUW1_6483434061

    //Retrieve match ids for player:
    // https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/<PLAYER PUUID>/ids?start=0&count=20

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
