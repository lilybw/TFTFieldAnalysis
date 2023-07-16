package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DefaultResponseRegistryService {

    public <T> ResponseEntity<DetailedResponse<T>> getResponseOnModelNotFound(int id){
        return new ResponseEntity<>(
                DetailedResponse.details(
                        new ResponseDetails("No Such Model", "No model with id: " + id + " exists", null)
                ),
                HttpStatusCode.valueOf(404)
        );
    }

    public <T> ResponseEntity<DetailedResponse<T>> getResponseOnRegistryMissing(){
        return new ResponseEntity<>(
                DetailedResponse.details(
                        new ResponseDetails("Service Unavailable", "The internal model registry service is unavailable.", null)
                ),
                HttpStatusCode.valueOf(500)
        );
    }

    public <T> ResponseEntity<DetailedResponse<T>> getResponseOnMissingPUUID(){
        return new ResponseEntity<>(
                DetailedResponse.details(
                        new ResponseDetails("Missing PUUID", "The route expected a player puuid, but recieved nothing", null)
                ),
                HttpStatusCode.valueOf(400)
        );
    }

}
