package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/model")
public class DataModelController {

    @Autowired
    private ModelRegistryService registry;

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<DetailedResponse<DataModel>> getModel(@PathVariable int id)
    {
        if(registry == null){
            return getResponseOnRegistryMissing();
        }

        DataModel modelFound = registry.retrieveModel(id);
        if(modelFound == null){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            null,
                            new ResponseDetails("No Such Model", "No model with id: " + id + " exists", null)
                    ),
                    HttpStatusCode.valueOf(404)
            );
        }
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        modelFound,
                        new ResponseDetails("Success", "", null)
                ), HttpStatusCode.valueOf(200)
        );
    }

    @GetMapping("/all")
    public @ResponseBody ResponseEntity<DetailedResponse<Set<Integer>>> getAllModelIds()
    {
        if(registry == null){
            return getResponseOnRegistryMissing();
        }

        return new ResponseEntity<>(
                new DetailedResponse<>(registry.getModelIds(),
                        new ResponseDetails("Success", "", null)),
                HttpStatusCode.valueOf(200)
        );
    }

    @PostMapping("/create")
    public @ResponseBody ResponseEntity<DetailedResponse<DataModel>> createModel()
    {
        DataModel model = registry.createModel();
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        model,
                        new ResponseDetails("Model Creation Successful", "", null)
                ),
                HttpStatusCode.valueOf(200)
        );
    }

    @PostMapping("/{id}/delete")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> deleteModel(@PathVariable int id){
        if(registry == null) {
            return getResponseOnRegistryMissing();
        }
        if(registry.deleteModel(id)){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            "",
                            new ResponseDetails("Successfully Deleted Model id: " + id, "", null)

                    ), HttpStatusCode.valueOf(200)
            );
        }
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        "",
                        new ResponseDetails(
                                "No Such Model", "", null
                        )
                ), HttpStatusCode.valueOf(404)
        );
    }

    private <T> ResponseEntity<DetailedResponse<T>> getResponseOnRegistryMissing(){
        return new ResponseEntity<>(new DetailedResponse<>((T) null,
                new ResponseDetails("Service Unavailable", "The internal model registry service is unavailable.", null)
            ), HttpStatusCode.valueOf(500)
        );
    }

}
