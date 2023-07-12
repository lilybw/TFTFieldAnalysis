package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.AutoFetch;
import gbw.riot.tftfieldanalysis.core.RequestsFile;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class SecretsService {

    private static String root;
    static {
        File file = new File(".");
        try{
            root = file.getCanonicalPath();
        }catch (IOException e){
            System.err.println("Project Root Not Found");
            e.printStackTrace();
        }
    }

    public ValueErrorTuple<String,Exception> getByKey(String key){
        try{
            BufferedReader br = new BufferedReader(new FileReader(root + "/secrets.txt"));
            String line;
            while((line = br.readLine()) != null){
                String[] split = line.split(":");
                split = ArrayUtil.forEach(split, String::trim);
                split = ArrayUtil.resizeStringArray(split, s -> !s.isEmpty());
                if(split.length < 2){
                    return ValueErrorTuple.error(new Exception("Incorrect k:v declaration for key: " + key));
                }
                if(split[0].equals(key)){
                    return ValueErrorTuple.value(split[1]);
                }
            }
        }catch (IOException e){
            return ValueErrorTuple.error(e);
        }
        return ValueErrorTuple.error(new Exception("No Such Key"));
    }

}
