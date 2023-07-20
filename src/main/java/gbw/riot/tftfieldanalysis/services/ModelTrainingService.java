package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.*;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Service
public class ModelTrainingService {
    @FunctionalInterface
    public interface ExcludeMatchFunction {
        boolean eval(MatchData match);
    }

    public enum ServerTargets{
        EUROPE("europe"), AMERICA("america"), ASIA("asia"), SEA("sea"), ERR_UNKNOWN("unknown");
        public final String target;
        ServerTargets(String target){
            this.target = target;
        }
        public static ServerTargets of(String string){
            for(ServerTargets target : ServerTargets.values()){
                if(target.target.equalsIgnoreCase(string)){
                    return target;
                }
            }
            return ServerTargets.ERR_UNKNOWN;
        }
    }

    public static class TrainingConfiguration{
        public int maxMatchCount = 10;
        public String patch = null;
        public boolean confineToBasePlayer = false;
        public ServerTargets serverTarget = ServerTargets.EUROPE;
    }

    public static class TrainingRegistration{
        public DataModel model;
        public long timeStart;
        public double progress;
        public TrainingConfiguration configuration;
    }

    private final ConcurrentMap<Integer,TrainingRegistration> modelsInTraining = new ConcurrentReferenceHashMap<>();

    @Autowired
    private DataRetrievalService retrievalService;

    public ValueErrorTuple<Set<String>,Exception> run(DataModel model, String puuid, TrainingConfiguration config){
        if(config == null || config.patch == null){
            return run(
                    model,
                    puuid,
                    config,
                    data -> false
            );
        }
        return run(
                model,
                puuid,
                config,
                data -> !data.metadata().data_version().equalsIgnoreCase(config.patch)
            );
    }

    public ValueErrorTuple<Set<String>,Exception> run(DataModel model, String puuid, TrainingConfiguration config, ExcludeMatchFunction excludeFunc){
        return run(
                model,
                config,
                puuid,
                excludeFunc,
                Set.of()
        );
    }

    public ValueErrorTuple<Set<String>,Exception> run(DataModel model, TrainingConfiguration config, String puuid, ExcludeMatchFunction excludeFunc, Set<String> excludedMatches){
        Exception configError = verifyConfig(config);
        if(configError != null){
            return ValueErrorTuple.error(configError);
        }

        long timeA = System.currentTimeMillis();
        LocalDateTime dateStart = LocalDateTime.now();
        ValueErrorTuple<Set<String>,Exception> result = retrievalService.start(
                config,
                puuid,
                match -> parseMatch(match, model, excludeFunc),
                excludedMatches
        );
        model.getMetaData().dateSecondsTrainingMap().add(
                new TrainingSession(dateStart, System.currentTimeMillis() - timeA)
        );
        return result;
    }

    private Exception verifyConfig(TrainingConfiguration config) {
        if(config == null){
            return new Exception("Missing config, a config can be empty, but must be included");
        }
        if(config.serverTarget == ServerTargets.ERR_UNKNOWN){
            return new Exception("Invalid server target. Valid ones are: "  + ArrayUtil.arrayJoinWith(ServerTargets.values(), ","));
        }
        return null;
    }

    private static final String[] defaultNoTrack = new String[]{"player","item"};

    private boolean parseMatch(MatchData data, DataModel model, ExcludeMatchFunction excludeFunc){
        if(excludeFunc.eval(data)){
            return false;
        }
        for(MatchData.Info.Participant participant : data.info().participants()){
            DataPoint playerAsPoint = model.insertPoint("player", Set.of(participant.puuid()),defaultNoTrack);
            DataPoint placementAsPoint = model.insertPoint("placement", Set.of(participant.placement() + ""),defaultNoTrack);
            DataPoint gametimeAsPoint = model.insertPoint("round", Set.of(participant.last_round() + ""), defaultNoTrack);
            model.insertOrIncrementEdge(placementAsPoint, playerAsPoint);

            for(MatchData.Info.Trait trait : participant.traits()){
                DataPoint traitAsPoint = model.insertPoint("trait", Set.of(trait.name(), trait.num_units() + ""), defaultNoTrack);
                model.insertOrIncrementEdge(traitAsPoint, playerAsPoint);
                model.insertOrIncrementEdge(traitAsPoint, placementAsPoint);
                model.insertOrIncrementEdge(traitAsPoint, gametimeAsPoint);
            }

            for(MatchData.Info.Unit unit : participant.units()){
                DataPoint unitAsPoint = model.insertPoint("unit", Set.of(unit.character_id(), unit.tier() + ""), defaultNoTrack);
                model.insertOrIncrementEdge(unitAsPoint, placementAsPoint);
                model.insertOrIncrementEdge(unitAsPoint, playerAsPoint);
                model.insertOrIncrementEdge(unitAsPoint, gametimeAsPoint);

                for(String item : unit.itemNames()){
                    DataPoint itemAsPoint = model.insertPoint("item", Set.of(item), defaultNoTrack);
                    model.insertOrIncrementEdge(itemAsPoint, unitAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, placementAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, playerAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, gametimeAsPoint);
                }
            }
        }
        model.getMetaData().matchIdsEvaluated().add(
                model.getMetaData().dictionary().insert(
                        data.metadata().match_id()
                )
        );

        return false;
    }
}
