package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ModelDTO(
        ModelMetaDataDTO metadata,
        List<String> namespaces,
        Map<Integer, Set<EdgeDTO>> pointIdEdgeSetMap,
        Map<String,Set<DataPointDTO>> namespacePointMap
        ) {

    public static ModelDTO of(DataModel model) {
        Dictionary<String> dictionary = model.getMetaData().dictionary();
        Map<String,Set<DataPoint>> decompressedPoints = dictionary.decompress(model.getNamespacePointMap());
        return new ModelDTO(
                ModelMetaDataDTO.of(model),
                dictionary.translateAll(model.getNamespaces()),
                EdgeDTO.of(model.getPointEdgeMap()),
                DataPointDTO.of(decompressedPoints, model)
        );
    }
}
