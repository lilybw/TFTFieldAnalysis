package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.ValErr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebServiceMetadataService {

    @Autowired
    private SecretsService secrets;

    public ValErr<String,Exception> getVersion(){
        return secrets.getSecret("Api-Version");
    }

}
