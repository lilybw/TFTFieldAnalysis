package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Edge;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class EdgeSetProcessingService {


    public Map<Integer, Set<Edge>> reduceResultMapToInclusions(String[] includedNamespaces, String[] includedTags, DataModel model, Map<Integer, Set<Edge>> edgesRaw) {
        if( //Early bail if there's no reduction parameters at all for this collection
            (includedNamespaces == null || includedNamespaces.length == 0)
                    &&
            (includedTags == null || includedTags.length == 0)
        ){
            return edgesRaw;
        }

        //reduce parameter arrays to what actually exists in the model - also: Null safety.
        int[] translatedIncludedNamespaces = includedNamespaces == null ? new int[0] : model.makeNamespacesComply(includedNamespaces);
        int[] translatedIncludedTags = includedTags == null ? new int[0] : model.makeTagsComply(includedTags);

        int calculatedSolution = translatedIncludedNamespaces.length > 0 ? 1 : 0;
        calculatedSolution += translatedIncludedTags.length > 0 ? 2 : 0;

        switch (calculatedSolution){
            case 1 -> { //excluded namespaces only
                edgesRaw = removeEdgeIfPointNIs(
                        edgesRaw,
                        (dp) -> ArrayUtil.contains(translatedIncludedNamespaces, dp.getNamespace())
                );
            }
            case 2 -> { //excluded tags only
                edgesRaw = removeEdgeIfPointNIs(
                        edgesRaw,
                        (dp) -> ArrayUtil.containsAnyOf(translatedIncludedTags,dp.getTags())
                );
            }
            case 3 -> { //both
                edgesRaw = removeEdgeIfPointNIs(
                        edgesRaw,
                        (dp) -> ArrayUtil.contains(
                                    translatedIncludedNamespaces,
                                    dp.getNamespace())
                                || //lesser (linear scaling) first to provoke lazy comparison
                                ArrayUtil.containsAnyOf(
                                        translatedIncludedTags,
                                        dp.getTags()
                                )
                );
            }
            default -> {
                System.err.println("Illegal edge-case hit at EdgeSetProcessingService.reduceResultMapToInclusions");
            }
        }
        return edgesRaw;
    }


    /**
     * Filters all edge sets in map
     * EXPECTS the id of the point to be disregarded to be the key of the entry in the map.
     * @param map where the key is the id of the point that edge set belongs to.
     * @return A map with the same keys where the value of each key has been replaced with a new, filtered version.
     */
    public Map<Integer, Set<Edge>> removeEdgeIfPointNIs(Map<Integer, Set<Edge>> map, Function<DataPoint,Boolean> includeOnTrue){
        map.replaceAll(
                (k, v) -> removeEdgeIfPointNIs(map.get(k), k, includeOnTrue));
        return map;
    }

    /**
     * Filtering function for an edge set for a given point
     * @param edgesRaw - the entire set
     * @param idOfPointToBeDisregarded - Id of said given point.
     * @param includeOnTrue - a function that returns true if said point should be included
     * @return a filtered set.
     */
    public Set<Edge> removeEdgeIfPointNIs(Set<Edge> edgesRaw, int idOfPointToBeDisregarded, Function<DataPoint,Boolean> includeOnTrue) {
        Set<Edge> toReturn = new HashSet<>();
        for(Edge edge : edgesRaw){
            DataPoint actuallyThatPoint = edge.pointA.getId() == idOfPointToBeDisregarded ? edge.pointB : edge.pointA;
            if(includeOnTrue.apply(actuallyThatPoint)){
               toReturn.add(edge);
            }
        }
        return toReturn;
    }

}
