package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PointCollectionProcessingService {

    /**
     * Query datapoints of model require all parameters met.
     * On any null or empty; any.
     * @param namespaces String[n] | String[0] | null
     * @param pointIds int[n] | int[0] | null
     * @param tags String[n] | String[0] | null
     * @param model DataModel
     * @return
     */
    public Set<DataPoint> queryRequireAll(String[] namespaces, int[] pointIds, String[] tags, DataModel model) {
        Set<DataPoint> toReturn = new HashSet<>();

        int computedResolution = 0;
        //"branchless" solution. Dear java. A bool is a bit. A bit is also the integer value 1. Lemme arithmetic plz
        computedResolution += namespaces == null || namespaces.length == 0 ? 0 : 1;
        computedResolution += pointIds == null || pointIds.length == 0 ? 0 : 2;
        computedResolution += tags == null || tags.length == 0 ? 0 : 4;

        //Null safety.
        namespaces = namespaces == null ? new String[0] : namespaces;
        tags = tags == null ? new String[0] : tags;

        switch (computedResolution) {
            case 0 -> //neither namespace nor pointIds declared
                    toReturn.addAll(model.getAllPoints());
            case 1 -> //namespace only
                    toReturn.addAll(model.getPointsInNamespaces(namespaces));
            case 2 -> //pointIds only
                    toReturn.addAll(model.getPointsWithIds(pointIds));
            case 3 -> //points in namespace of ids pointIds
                    toReturn.addAll(queryOnNamespacesAndIds(namespaces, pointIds, model));
            case 4 -> {
                    toReturn.addAll(model.getPointsWithTags(tags));
            }
            case 5 -> {//points in namespace x with tags x,y,z
                    toReturn.addAll(queryOnNamespacesAndTags(namespaces, tags, model));
            }
            case 6 -> {  //points with ids x,y,z and tags x,y,z
                    toReturn.addAll(queryOnIdsAndTags(pointIds, tags, model));
            }
            case 7 -> { //points with ids x,y,z and tags x,y,z within namespace x
                    toReturn.addAll(queryOnIdsTagsAndNamespaces(namespaces, pointIds, tags, model));
            }
        }
        return toReturn;
    }

    public Set<DataPoint> queryOnNamespacesAndIds(String[] namespaces, int[] ids, DataModel model){
        return ArrayUtil.resize(
                model.getPointsInNamespaces(namespaces),
                point -> ArrayUtil.contains(ids, point.getId())
        );
    }

    public Set<DataPoint> queryOnNamespacesAndTags(String[] namespaces, String[] tags, DataModel model){
        int[] translatedTagsForComparison = model.getMetaData().dictionary().reverseTranslateAll(tags);

        return ArrayUtil.resize(
                model.getPointsInNamespaces(namespaces),
                point -> ArrayUtil.containsAnyOf(translatedTagsForComparison, point.getTags())
        );
    }

    public Set<DataPoint> queryOnIdsAndTags(int[] ids, String[] tags, DataModel model) {
        int[] translatedTagsForComparison = model.getMetaData().dictionary().reverseTranslateAll(tags);
        return ArrayUtil.resize(
                model.getPointsWithIds(ids),
                point -> ArrayUtil.containsAnyOf(translatedTagsForComparison, point.getTags())
        );
    }

    public Set<DataPoint> queryOnIdsTagsAndNamespaces(String[] namespaces, int[] pointIds, String[] tags, DataModel model){
        int[] translatedTagsForComparison = model.getMetaData().dictionary().reverseTranslateAll(tags);
        int[] translatedNamespacesForComparison = model.getMetaData().dictionary().reverseTranslateAll(namespaces);

        Set<DataPoint> withTags = model.getPointsWithTags(tags);
        Set<DataPoint> withIds = model.getPointsWithIds(pointIds);
        Set<DataPoint> toReturn;
        if(withIds.size() <= withTags.size()){
            toReturn = withIds.stream()
                    .filter(point ->
                            ArrayUtil.contains(translatedNamespacesForComparison,point.getNamespace())
                    )
                    .filter(point -> ArrayUtil.containsAnyOf(translatedTagsForComparison,point.getTags()))
                    .collect(Collectors.toSet());
        }else {
            toReturn = withTags.stream()
                    .filter(point ->
                            ArrayUtil.contains(translatedNamespacesForComparison,point.getNamespace())
                    )
                    .filter(point -> ArrayUtil.isAnyOf(point.getId(), pointIds))
                    .collect(Collectors.toSet());
        }
        return toReturn;
    }

}
