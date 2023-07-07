package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.JSONWrapper;
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

    private final RestTemplate getMatchDataTemplate, getMatchIdsTemplate;
    private final String basePlayer = "PbehKkjRrNApiTrB_Q5IH5a0EAozAHNRFdd_ObZQW1c4Pt3ZL22A-gt1kFPOaxpERXRCPSQWpy7kNQ";

    public DataRetrievalService(RestTemplateBuilder restTemplateBuilder) {
        this.getMatchDataTemplate = restTemplateBuilder.build();
        this.getMatchIdsTemplate = restTemplateBuilder.build();
    }

    private HttpEntity<?> getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", "secret");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>("",headers);
    }

    public MatchData getMatchData(String matchId) {
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/"+matchId;
        ResponseEntity<MatchData> response =  this.getMatchDataTemplate.exchange(url, HttpMethod.GET, getHeaders(), MatchData.class);
        return response.getBody();
    }

    public String[] getMatchIdsForPlayer(String playerPUUID){
        String url = "https://europe.api.riotgames.com/tft/match/v1/matches/by-puuid/"+playerPUUID+"/ids?start=0&count=20";
        String fullUnparsed = this.getMatchIdsTemplate.exchange(url, HttpMethod.GET, getHeaders(), String.class).getBody();
        return fullUnparsed == null ? new String[0] : JSONWrapper.parseValueArray(fullUnparsed);
    }

    public HashSet<String> run(int maxCount, MatchParser forEachMatch){
        HashSet<String> playersParsed = new HashSet<>();
        HashSet<String> matchesParsed = new HashSet<>();

        Queue<String> playersYetToParse = new LinkedBlockingQueue<>(Collections.singleton(basePlayer));

        int playersParsedCount = 0;
        while(playersParsedCount < maxCount && !playersYetToParse.isEmpty()){

            String player = playersYetToParse.poll();
            if(player == null){
                break;
            }

            String[] matchIds = getMatchIdsForPlayer(player);
            matchIds = ArrayUtil.resizeStringArray(matchIds, match -> !matchesParsed.contains(match));

            for(String id : matchIds){
                MatchData match = getMatchData(id);
                boolean abort = forEachMatch.apply(match);
                matchesParsed.add(id);
                if(abort) return matchesParsed;

                //collection difference
                List<String> possibleNewPlayers = match.metadata().participants();
                String[] uniqueNewPlayers = ArrayUtil.resizeStringArray(
                        possibleNewPlayers.toArray(new String[0]),
                        p -> !playersParsed.contains(p)
                );
                playersYetToParse.addAll(List.of(uniqueNewPlayers));
            }

            playersParsed.add(player);
        }
        return matchesParsed;
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
