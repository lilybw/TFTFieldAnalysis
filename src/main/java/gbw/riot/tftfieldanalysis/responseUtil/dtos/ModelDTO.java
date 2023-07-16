package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;
import gbw.riot.tftfieldanalysis.responseUtil.MapUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(name = "ModelDTO")
public record ModelDTO(
        ModelMetaDataDTO metadata,
        List<String> namespaces,
        Map<Integer, List<EdgeDTO>> pointIdEdgeSetMap,
        Map<String, List<DataPointDTO>> namespacePointMap
        ) {

    public static ModelDTO of(DataModel model) {
        Dictionary<String> dictionary = model.getMetaData().dictionary();
        Map<String,List<DataPoint>> decompressedPoints = MapUtil.Values.setToList(
                dictionary.decompress(
                        model.getNamespacePointMap()
                )
        );

        return new ModelDTO(
                ModelMetaDataDTO.of(model),
                dictionary.translateAll(model.getNamespaces()),
                EdgeDTO.of(model.getPointEdgeMap()),
                DataPointDTO.of(decompressedPoints, model)
        );
    }
}
