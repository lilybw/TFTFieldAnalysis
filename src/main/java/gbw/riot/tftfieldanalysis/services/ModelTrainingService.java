package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.core.ValueErrorTouple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class ModelTrainingService {

    @Autowired
    private DataRetrievalService retrievalService;

    @FunctionalInterface
    public interface ExcludeMatchFunction {
        boolean eval(MatchData match);
    }

    public ValueErrorTouple<Set<String>,Exception> run(DataModel model, int maxMatchCount){
        return run(
                model,
                maxMatchCount,
                data -> false
        );
    }

    public ValueErrorTouple<Set<String>,Exception> run(DataModel model, int maxMatchCount, String patch){
        return run(
                model,
                maxMatchCount,
                data -> !data.metadata().data_version().equalsIgnoreCase(patch)
            );
    }

    public ValueErrorTouple<Set<String>,Exception> run(DataModel model, int maxMatchCount, ExcludeMatchFunction excludeFunc){
        return run(
                model,
                maxMatchCount,
                excludeFunc,
                Set.of()
        );
    }

    public ValueErrorTouple<Set<String>,Exception> run(DataModel model, int maxMatchCount, ExcludeMatchFunction excludeFunc,  Set<String> excludedMatches){
        long timeA = System.currentTimeMillis();
        LocalDateTime dateStart = LocalDateTime.now();
        ValueErrorTouple<Set<String>,Exception> result = retrievalService.run(
                maxMatchCount,
                match -> parseMatch(match, model, excludeFunc),
                excludedMatches
        );
        model.getMetaData().dateSecondsTrainingMap().add(
                new DataModel.TrainingSession(dateStart, System.currentTimeMillis() - timeA)
        );
        return result;
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
        model.getMetaData().matchIdsEvaluated().add(data.metadata().match_id());

        return false;
    }
}
