package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;

import java.util.List;
import java.util.Map;

public record ModelMetaDataDTO(
        int modelId,
        List<String> matchIdsEvaluated,
        List<DataModel.TrainingSession> dateSecondsTrainingMap,
        Map<DataModel.CacheKeys,Long> cachedValues,
        Map<String,Long> pointsPerNamespace,
        Map<String,Long> pointsWithTagCount
        ) {
    public static ModelMetaDataDTO of(DataModel model){
        DataModel.ModelMetaData data = model.getMetaData();
        Dictionary<String> dictionary = data.dictionary();

        return new ModelMetaDataDTO(
                data.modelId(),
                dictionary.translateAll(data.matchIdsEvaluated()),
                data.dateSecondsTrainingMap(),
                data.cachedValues(),
                dictionary.decompress(data.pointsPerNamespace()),
                dictionary.decompress(data.pointsWithTagCount())
        );
    }
}
