package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.CacheKeys;
import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.ModelMetaData;
import gbw.riot.tftfieldanalysis.core.TrainingSession;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(name = "ModelMetaDataDTO")
public record ModelMetaDataDTO(
        int modelId,
        List<String> matchIdsEvaluated,
        List<TrainingSession> dateSecondsTrainingMap,
        Map<CacheKeys,Long> cachedValues,
        Map<String,Long> pointsPerNamespace,
        Map<String,Long> pointsWithTagCount
        ) {
    public static ModelMetaDataDTO of(DataModel model){
        ModelMetaData data = model.getMetaData();
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
