package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.MatchData;
import gbw.riot.tftfieldanalysis.core.ValueErrorTouple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return retrievalService.run(
                maxMatchCount,
                match -> parseMatch(match, model,
                        data -> false
                )
        );
    }

    public ValueErrorTouple<Set<String>,Exception> run(DataModel model, int maxMatchCount, String patch){
        return retrievalService.run(
                maxMatchCount,
                match -> parseMatch(match, model,
                    data -> !data.metadata().data_version().equalsIgnoreCase(patch)
            )
        );
    }

    private boolean parseMatch(MatchData data, DataModel model, ExcludeMatchFunction excludeFunc){
        if(excludeFunc.eval(data)){
            return false;
        }
        for(MatchData.Info.Participant participant : data.info().participants()){
            DataPoint playerAsPoint = model.insertPoint("player", List.of(participant.puuid()));
            DataPoint placementAsPoint = model.insertPoint("placement", List.of(participant.placement() + ""));
            DataPoint gametimeAsPoint = model.insertPoint("round", List.of(participant.last_round() + ""));
            model.insertOrIncrementEdge(placementAsPoint, playerAsPoint);

            for(MatchData.Info.Trait trait : participant.traits()){
                DataPoint traitAsPoint = model.insertPoint("trait", List.of(trait.name(), trait.num_units() + ""));
                model.insertOrIncrementEdge(traitAsPoint, playerAsPoint);
                model.insertOrIncrementEdge(traitAsPoint, placementAsPoint);
                model.insertOrIncrementEdge(traitAsPoint, gametimeAsPoint);
            }

            for(MatchData.Info.Unit unit : participant.units()){
                DataPoint unitAsPoint = model.insertPoint("unit", List.of(unit.name(), unit.tier() + ""));
                model.insertOrIncrementEdge(unitAsPoint, placementAsPoint);
                model.insertOrIncrementEdge(unitAsPoint, playerAsPoint);
                model.insertOrIncrementEdge(unitAsPoint, gametimeAsPoint);

                for(String item : unit.itemNames()){
                    DataPoint itemAsPoint = model.insertPoint("item", List.of(item));
                    model.insertOrIncrementEdge(itemAsPoint, unitAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, placementAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, playerAsPoint);
                    model.insertOrIncrementEdge(itemAsPoint, gametimeAsPoint);
                }
            }
        }
        model.addMatchId(data.metadata().match_id());

        return false;
    }
}
