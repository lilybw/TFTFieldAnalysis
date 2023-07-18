package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

//Group Operation Utility
public class GroupOperationUtil {

    //Group Operation Result
    public record GOResult<T>(Collection<T> intersection, Collection<T> difference){}

    public static <T> GOResult<T> intersection(Collection<T> groupA, Collection<T> groupB){
        Set<T> intersection = new HashSet<>();
        Set<T> difference = new HashSet<>(groupA); // Start with all elements from collection1

        for (T element : groupB) {
            if (difference.contains(element)) {
                // If the element is in the difference set, it means it's not an intersection.
                // Remove it from the difference set and add it to the intersection set.
                difference.remove(element);
                intersection.add(element);
            } else {
                // If the element is not in the difference set, it's an intersection.
                intersection.add(element);
            }
        }
        return new GOResult<>(intersection,difference);
    }

    public static <T> GOResult<T> intersection(T[] groupA, T[] groupB){
        Set<T> intersection = new HashSet<>();
        Set<T> difference = new HashSet<T>(List.of(groupA)); // Start with all elements from collection1

        for (T element : groupB) {
            if (difference.contains(element)) {
                // If the element is in the difference set, it means it's not an intersection.
                // Remove it from the difference set and add it to the intersection set.
                difference.remove(element);
                intersection.add(element);
            } else {
                // If the element is not in the difference set, it's an intersection.
                intersection.add(element);
            }
        }
        return new GOResult<>(intersection,difference);
    }

}
