package gbw.riot.tftfieldanalysis.responseUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONWrapper {

    private Map<String, String> level1;

    public static String error(int status, String message){
        return "{\n"+
                "\t\"status\" : "+status+",\n"+
                "\t\"message\" : \""+message+"\"\n"+
                "}";
    }
    public static String success(int status, String message){
        return "{\n"+
                "\t\"status\" : "+status+",\n"+
                "\t\"message\" : \""+message+"\"\n"+
                "}";
    }
    public static String custom(int status, Map<String,String> kvPairs){
        String toReturn = "{\"status\" : " +status +"}";
        return JSONModifier.addFields(toReturn, kvPairs);
    }

    /**
     * Parses a json object with no nested objects or arrays of objects or values.
     * Faster than parseObject.
     */
    public static Map<String, String> parseSimpleObject(String string){
        List<String> asArray = new ArrayList<>(List.of(JSONModifier.unitMask(string,new Character[]{'{','}'}).split(",")));
        Map<String, String> toReturn = new HashMap<>();
        for(String s : asArray){
            String[] kv = JSONModifier.unitMask(s,new Character[]{'\"'}).split(":");
            String value = "unavailable";
            if(kv.length > 1){
                value = kv[1].trim();
            }
            toReturn.put(kv[0].trim(),value);
        }
        return toReturn;
    }

    public static Map<String,String> parseObject(String json){ //doesnt work
        Map<String, String> map = new HashMap<>();
        int startIndex = json.indexOf("{");
        int endIndex = json.lastIndexOf("}");
        json = json.substring(startIndex + 1, endIndex);
        String[] keyValuePairs = json.split(",(?![^{}\\[\\]]*(\\{|\\[)[^{}\\[\\]]*[]}][^{}\\[\\]]*(}|]))");
        ArrayUtil.print(keyValuePairs);

        for (String pair : keyValuePairs) {
            int colonIndex = pair.indexOf(":");
            String key = pair.substring(0, colonIndex).trim();
            String value = pair.substring(colonIndex + 1).trim();

            if (value.startsWith("{") && value.endsWith("}")) {
                Map<String, String> nestedMap = parseObject(value);
                for (String nestedKey : nestedMap.keySet()) {
                    map.put(key + "." + nestedKey, nestedMap.get(nestedKey));
                }
            } else if (value.startsWith("[") && value.endsWith("]")) {
                String[] arrayValues = value.substring(1, value.length() - 1).split(",");
                for (int i = 0; i < arrayValues.length; i++) {
                    arrayValues[i] = arrayValues[i].trim();
                }
                map.put(key, String.join(",", arrayValues));
            } else {
                value = value.replaceAll("\"", "");
                map.put(key, value);
            }
        }
        return map;
    }


    public static String[] parseObjectArray(String string){
        String unitMasked = JSONModifier.unitMask(string, new Character[]{'[',']'});
        String[] jsonObjects = unitMasked.split("(?<=\\}),(?=\\{)");
        for (int i = 0; i < jsonObjects.length; i++) {
            jsonObjects[i] = jsonObjects[i].trim();
        }
        return jsonObjects;
    }

    public static String[] parseValueArray(String string){
        String[] splitUncleaned = string.split(",");
        String[] cleansed = new String[splitUncleaned.length];
        for(int i = 0; i < splitUncleaned.length; i++){
            cleansed[i] = JSONModifier.unitMask(splitUncleaned[i].trim(), new Character[]{'"'});
        }
        return cleansed;
    }

    public JSONWrapper(String string){
        level1 = parseSimpleObject(string);
    }

    public String getOr(String name, String valueOnFail){
        String value = this.get(name);
        if(value == null || value.equals("unavailable") || value.isEmpty()){
            return valueOnFail;
        }
        return value;
    }
    public <T> T getOr(T type, String s, String valueOnFail){
        String value = getOr(s,valueOnFail);
        T asType = null;
        try {
            asType = (T) type.getClass().getConstructor(String.class).newInstance(s);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return asType;
    }

    public String get(String name){
        return level1.get(name);
    }
    public String getLike(String name){
        String nameTest = name;
        if(this.get(nameTest) != null){
            return this.get(nameTest);
        }
        nameTest = name.toLowerCase();
        if(this.get(nameTest) != null){
            return this.get(nameTest);
        }
        nameTest = name.toUpperCase();
        if(this.get(nameTest) != null){
            return this.get(nameTest);
        }
        nameTest = StringUtil.toCamel(name);
        if(this.get(nameTest) != null){
            return this.get(nameTest);
        }
        return null;
    }



}
