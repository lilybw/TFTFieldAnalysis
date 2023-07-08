package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Edge;

import java.util.Map;
import java.util.Set;

public record ModelDTO(
        int modelId,
        Map<DataModel.CacheKeys, Long> cache,
        Map<Integer, Set<Edge>> edgePointIdMap,
        Map<String,Set<DataPoint>> namespacePointMap,
        Set<String> namespaces,
        Set<String> dataset
        ) {
    public static ModelDTO of(DataModel model) {
        return new ModelDTO(
                model.getId(),
                model.getCachedValues(),
                model.getPointEdgeMap(),
                model.getNamespacePointMap(),
                model.getNamespaces(),
                model.getEvaluatedMatches()
        );
    }
}
