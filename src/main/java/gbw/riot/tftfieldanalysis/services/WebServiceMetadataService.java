package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebServiceMetadataService {

    @Autowired
    private SecretsService secrets;

    public ValueErrorTuple<String,Exception> getVersion(){
        return secrets.getByKey("Api-Version");
    }

}
