package gbw.riot.tftfieldanalysis.responseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//imported from gbw TheScheduler
/**
 * Works with standard JSON format purely by String operations
 * expects input json to be the same format as when an object is converted
 * using jackson.
 */
public class JSONModifier {

    /**
     * Removes the field and value of specified attribute
     * @param attributeName name of attribute without quotation-marks
     * @param json Full json string
     * @return A new json string with that field removed.
     */
    public static String removeField(String attributeName, String json){
        String[] asArray = unitMask(json,new Character[]{'{','}'}).split(",");
        asArray = arrayFilterAttribute(asArray, attributeName);
        return "{" + ArrayUtil.arrayJoinWith(asArray,",") + "}";
    }

    /**
     * Replaces the value of a json attribute
     * @param attributeName what attribute
     * @param json complete json string
     * @param mask Replace string without quotation-marks
     * @return A new json string with that field masked.
     */
    public static String maskField(String attributeName, String json, String mask){
        String[] asArray = unitMask(json,new Character[]{'{','}'}).split(",");
        List<String> toReturn = new ArrayList<>();

        for(String s : asArray){
            if(s.contains("\""+attributeName+"\"")){
                String[] temp = s.split(":");
                toReturn.add(temp[0] + ":" + "\"" + mask + "\"");
            }else {
                toReturn.add(s);
            }
        }

        return "{" + ArrayUtil.arrayJoinWith(toReturn.toArray(new String[0]),",") + "}";
    }
    public static String addField(String json, String key, String value) {
        List<String> toReturn = new ArrayList<>(List.of(unitMask(json,new Character[]{'{','}'}).split(",")));

        String nullChecked = value == null ? "" : value;
        toReturn.add("\""+key+"\" : \"" + nullChecked + "\"");

        return "{" + ArrayUtil.arrayJoinWith(toReturn.toArray(new String[0]),",") + "}";
    }
    public static String addFields(String json, Map<String,String> kvPais){
        List<String> toReturn = new ArrayList<>(List.of(unitMask(json,new Character[]{'{','}'}).split(",")));

        for(String key: kvPais.keySet()) {
            String nullChecked = kvPais.get(key) == null ? "" : kvPais.get(key);
            toReturn.add("\"" + key + "\" : \"" + nullChecked + "\"");
        }

        return "{" + ArrayUtil.arrayJoinWith(toReturn.toArray(new String[0]),",") + "}";
    }

    private static String[] arrayFilterAttribute(String[] array, String attribute){
        List<String> toReturn = new ArrayList<>();

        for(String s : array){
            if(!s.contains("\""+attribute+"\"")){
                toReturn.add(s);
            }
        }

        return toReturn.toArray(new String[0]);
    }

    public static String unitMask(String string, Character[] mask){
        for(Character c : mask){
            string = string.replaceAll("" + c,"");
        }
        return string;
    }



}
