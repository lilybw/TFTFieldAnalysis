package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static gbw.riot.tftfieldanalysis.responseUtil.IntUtil.parseOr;


//imported from gbw TheScheduler
public class ArrayUtil {



    @FunctionalInterface
    public interface BooleanFunction<T> {
        boolean eval(T o);
    }
    @FunctionalInterface
    public interface GenericComparator<T,R>{
        boolean eval(T o1, R o2);
    }
    @FunctionalInterface
    public interface VoidFunction<T> {
        void eval(T o);
    }

    /**
     * Function checking if a given object is a valid Integer using Integer.parseInt(Obj)
     */
    public static final BooleanFunction<Integer> INTEGER_INCLUDE = e -> {
        try {
            Integer.parseInt("" + e);
            return true;
        } catch (NullPointerException | NumberFormatException ex) {
            return false;
        }
    };
    public static final BooleanFunction<Integer> INTEGER_IGNORE = e -> !INTEGER_INCLUDE.eval(e);
    public static final BooleanFunction<String> EMPTY_STRING_IGNORE = e -> !e.isEmpty();

    /**
     * Turns a String array into an int array.
     * @param arr Array to parse
     * @return int[]
     */
    public static int[] parseIntArray(String[] arr) throws NumberFormatException, NullPointerException {
        return Stream.of(arr).mapToInt(Integer::parseInt).toArray();
    }

    public static <T> T[] removeTail(T[] arr, int len){
        return Arrays.copyOf(arr,arr.length - len);
    }

    public static <T extends Comparable<? super T>> List<T> sort(Collection<T> c, Comparator<? super T> comparator) {
        List<T> list = new ArrayList<T>(c);
        list.sort(comparator);
        return list;
    }

    @SuppressWarnings("unchecked")
    public static int[] parseIntArray(Object[] arr, BooleanFunction includeFunc){
        arr = resize(arr, includeFunc);
        int[] toReturn = new int[arr.length];

        for(int i = 0; i < toReturn.length; i++){
            toReturn[i] = parseOr(arr[i],-1); //unnecessary parsing, but I know no way around it.
        }

        return toReturn;
    }
    public static Long[] fromIntToLong(int[] intArr){
        Long[] toReturn = new Long[intArr.length];
        for(int i = 0; i < toReturn.length; i++){
            toReturn[i] = (long) intArr[i];
        }
        return toReturn;
    }
    public static <T> boolean containsAnyOf(Set<T> set1, Set<T> set2){
        for(T element : set1){
            if(set2.contains(element)){
                return true;
            }
        }
        return false;
    }

    public static boolean containsAnyOf(String[] arr1, Set<String> arr2) {
        for(String s : arr1){
            if(arr2.contains(s)){
                return true;
            }
        }
        return false;
    }
    public static <T> boolean containsAnyOf(T[] arr1, T[] arr2){
        return containsAnyOf(arr1, arr2, (o1, o2) -> o1 == o2);
    }
    public static <T> boolean containsAnyOf(int[] arr1, Set<Integer> arr2){
        for(int i : arr1){
            if(arr2.contains(i)){
                return true;
            }
        }
        return false;
    }

    /**
     * @return whether array 1 contains any one element of array 2 or not.
     */
    public static <T,R> boolean containsAnyOf(T[] arr1, R[] arr2, GenericComparator<T,R> comparator){
        for(int i = 0; i < arr1.length; i++){
            for(int j = i; j < arr2.length; j++){
                if(comparator.eval(arr1[i],arr2[i])){
                    return true;
                }
            }
        }
        return false;
    }
    public static <T> boolean isAnyOf(T element, Collection<T> collection){
        return collection.contains(element);
    }
    public static <T,R> boolean isAnyOf(R element, T[] array, GenericComparator<T,R> comparator){
        for(T obj : array){
            if(comparator.eval(obj,element)){
                return true;
            }
        }
        return false;
    }
    public static boolean isAnyOf(int i, int[] array){
        for(int i2 : array){
            if(i == i2){
                return true;
            }
        }
        return false;
    }

    /**
     * @return wether or not array 1 contains all elements of array 2. It may contain more.
     */
    public static <T,R> boolean containsAllOf(T[] arr1, R[] arr2, GenericComparator<T,R> comparator){
        for(R obj : arr2){
            boolean equivalentFound = false;
            for(T obj2 : arr1){
                if(comparator.eval(obj2, obj)){
                    equivalentFound = true;
                    break;
                }
            }
            if(!equivalentFound){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(parseIntArray(new Object[]{"1", "2"}, INTEGER_INCLUDE)));
        System.out.println(Arrays.toString(parseIntArray(new Object[]{"1","hi", "", "2"}, INTEGER_INCLUDE)));
        System.out.println(Arrays.toString(parseIntArray(new Object[]{"1000000000","1","hi", "", "-1", "2"}, INTEGER_INCLUDE)));
        System.out.println(Arrays.toString(parseIntArray(new Object[]{"1000000000000000","1000000000","1","hi", "", "-1", "2"}, INTEGER_INCLUDE)));
    }

    public static <T> T[] resize(T[] arr, BooleanFunction<T> includeFunc){
        return filter(arr, includeFunc, obj -> {});
    }
    public static <T> Set<T> resize(Set<T> set, BooleanFunction<T> includeFunc){
        Set<T> toReturn = new HashSet<>();
        set.forEach(obj -> {
                    if (includeFunc.eval(obj)) {
                        toReturn.add(obj);
                    }
                }
        );
        return toReturn;
    }
    public static int[] resize(int[] array, BooleanFunction<Integer> includeFunc){
        List<Integer> resized = new ArrayList<>();
        for(int i : array){
            if(includeFunc.eval(i)){
                resized.add(i);
            }
        }
        int[] asPrimitive = new int[resized.size()];
        for(int i = 0; i < asPrimitive.length; i++){
            asPrimitive[i] = resized.get(i);
        }
        return asPrimitive;
    }
    public static <T> T[] filter(T[] arr, BooleanFunction<T> filterFunc, VoidFunction<T> onFilterEvent){
        List<T> toReturn = new ArrayList<>();
        for(T obj : arr){
            if(filterFunc.eval(obj)){
                toReturn.add(obj);
                onFilterEvent.eval(obj);
            }
        }
        return (T[]) toReturn.toArray(); //oh java, you poor thing.
    }
    public static String[] resizeStringArray(String[] array, BooleanFunction<String> includeFunc){
        List<String> toReturn = new ArrayList<>();
        for(String s : array){
            if(includeFunc.eval(s)){
                toReturn.add(s);
            }
        }
        return toReturn.toArray(new String[0]);
    }

    public static int countLengthIgnore(Object[] arr, BooleanFunction<Object> func){
        int count = 0;
        for(Object o : arr){
            if(func.eval(o)){
                count++;
            }
        }
        return count;
    }

    /**
     * Not in place for each
     * @param arr
     * @param forEach
     * @param <T>
     * @return
     */
    public static <T> T[] forEach(T[] arr, Function<T,T> forEach){
        T[] toReturn = Arrays.copyOf(arr, arr.length);
        for(int i = 0; i < arr.length; i++){
            toReturn[i] = forEach.apply(arr[i]);
        }
        return toReturn;
    }

    public static String arrayJoin(String[] array){
        StringBuilder sb = new StringBuilder();
        for(String s : array){
            sb.append(s);
        }
        return sb.toString();
    }

    public static void print(String[] array){
        System.out.println("String array " + array + " contains :");
        for(String s : array){
            System.out.println(s);
        }
    }

    public static <T> String arrayJoinWith(List<T> list, Function<T,String> toStringFunction, String joint){
        String[] asArray = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            asArray[i] = toStringFunction.apply(list.get(i));
        }
        return arrayJoinWith(asArray,joint);
    }

    /**
     * Joins each index, regardless of content, with the given joint so that
     * for given joint "," array [a][b][c] becomes "a,b,c"
     * @param array
     * @param joint
     * @return the array as a string joined with the given joint
     */
    public static String arrayJoinWith(String[] array, String joint){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < array.length -1; i++){
            sb.append(array[i]).append(joint);
        }
        if(array.length > 0)
            sb.append(array[array.length-1]);
        return sb.toString();
    }
    /**
     * Joins each index, regardless of content, with the given joint so that
     * for given joint "," array [a][b][c] becomes "a,b,c"
     * @param array
     * @param joint
     * @return the array as a string joined with the given joint
     */
    public static String arrayJoinWith(Object[] array, String joint){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < array.length -1; i++){
            sb.append(array[i]).append(joint);
        }
        if(array.length > 0)
            sb.append(array[array.length-1]);
        return sb.toString();
    }
    /**
     * Joins each index, regardless of content, with the givent joint so that
     * for given joint "," array [a][b][c] becomes "a,b,c"
     * @param array
     * @param joint
     * @return the array as a string joined with the given joint
     */
    public static String arrayJoinWith(int[] array, String joint){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < array.length -1; i++){
            sb.append(array[i]).append(joint);
        }
        sb.append(array[array.length-1]);
        return sb.toString();
    }
    public static boolean contains(String[] array, String string){
        for(String s : array){
            if(s.equals(string)){
                return true;
            }
        }
        return false;
    }
    public static boolean contains(int[] array, int i2){
        for(int i1 : array){
            if(i1 == i2){
                return true;
            }
        }
        return false;
    }

    public static String[] fromIntArrayToString(Integer[] arr){
        return fromIntArrayToString(
                Arrays.stream(arr).mapToInt(Integer::intValue).toArray()
        );
    }

    public static String[] fromIntArrayToString(int[] arr){
        return Arrays.stream(arr)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
    }

    public static List<String> fromIntArrayToStringList(int[] arr){
        return Arrays.stream(arr).mapToObj(i -> i + "").toList();
    }
    public static List<String> fromIntArrayToStringList(Integer[] arr){
        return Arrays.stream(arr).map(String::valueOf).toList();
    }

    /**
     * Return a resized array without the indexes matching the given string
     * @param array
     * @param string
     * @return
     */
    public static String[] removeIndexMatching(String[] array, String string){
        List<String> toReturn = new ArrayList<>();
        for(String s : array){
            if(!s.equals(string)){
                toReturn.add(s);
            }
        }
        return toReturn.toArray(new String[0]);
    }
    public static int[] removeIndexMatching(int[] array, int i2){
        List<Integer> toReturn = new ArrayList<>();
        for(int i1 : array){
            if(i1 != i2){
                toReturn.add(i2);
            }
        }
        return toReturn.stream().mapToInt(Integer::intValue).toArray();
    }

}
