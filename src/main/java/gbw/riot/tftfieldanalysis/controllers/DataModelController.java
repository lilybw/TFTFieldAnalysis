package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Edge;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/model")
public class DataModelController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private ModelTrainingService trainer;

    @PostMapping("/{id}/train")
    public @ResponseBody ResponseEntity<DetailedResponse<Integer>> trainModel(@PathVariable int id, @RequestParam int maxMatchCount, @RequestParam String patch)
    {
        if(registry == null){
            return getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return getResponseOnModelNotFound(id);
        }
        trainer.run(model, maxMatchCount, patch);
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        model.getId(),
                        new ResponseDetails("Training Begun", "Model id: " + id + " is now being trained.",
                                List.of("Model: " + id, "maxMatchCount: " + maxMatchCount, "patch: " + patch)
                        )
                ), HttpStatusCode.valueOf(200)
        );
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<DetailedResponse<DataModel>> getModel(@PathVariable int id)
    {
        if(registry == null){
            return getResponseOnRegistryMissing();
        }

        DataModel modelFound = registry.retrieveModel(id);
        if(modelFound == null){
            return getResponseOnModelNotFound(id);
        }
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        modelFound,
                        ResponseDetails.SUCCESS
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
                new DetailedResponse<>(
                        registry.getModelIds(),
                        ResponseDetails.SUCCESS
                ),
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
        return getResponseOnModelNotFound(id);
    }

    @GetMapping("/{id}/points")
    public @ResponseBody ResponseEntity<DetailedResponse<Set<DataPoint>>> getPoints(@PathVariable int id, @RequestParam String namespace){
        if(registry == null){
            return getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return getResponseOnModelNotFound(id);
        }

        if(namespace == null || namespace.length() == 0){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            model.getAllPoints(),
                            ResponseDetails.SUCCESS
                    ),
                    HttpStatusCode.valueOf(200)
            );
        }

        return new ResponseEntity<>(
                new DetailedResponse<>(
                        model.getPointsInNamespace(namespace),
                        ResponseDetails.SUCCESS
                ), HttpStatusCode.valueOf(200)
        );
    }

    @GetMapping("/{id}/edges")
    public @ResponseBody ResponseEntity<DetailedResponse<Map<Integer, Set<Edge>>>> getEdgeSets(@RequestParam Integer[] points, @PathVariable int id)
    {
        if(registry == null){
            return getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return getResponseOnModelNotFound(id);
        }

        if(points == null || points.length == 0){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            null,
                            new ResponseDetails(
                                    "Missing Ids",
                                    "Please provide valid point ids as query parameter \"...url...?points=x,y,z\"",
                                    null
                            )
                    ), HttpStatusCode.valueOf(404)
            );
        }

        Integer[] pointsThatDoesNotExist = new Integer[0];
        Integer[] pointsThatDoesExist = ArrayUtil.resize(points, point -> model.getPointMap().get(point) != null);
        boolean invalidPointsIncluded = false;
        if(pointsThatDoesExist.length != points.length){
            pointsThatDoesNotExist = ArrayUtil.resize(points, point -> model.getPointMap().get(point) == null);
            invalidPointsIncluded = true;
        }

        if(pointsThatDoesExist.length == 0){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            new HashMap<>(),
                            new ResponseDetails(
                                    "No Valid Points Included", "", null
                            )
                    ), HttpStatusCode.valueOf(400)
            );
        }

        if(invalidPointsIncluded){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            model.getEdgesForPoints(pointsThatDoesExist),
                            new ResponseDetails(
                                    "Partial Success", "Some points did not exist in model", ArrayUtil.fromIntArrayToStringList(pointsThatDoesNotExist)
                            )
                    ), HttpStatusCode.valueOf(200)
            );
        }

        return new ResponseEntity<>(
                new DetailedResponse<>(
                        model.getEdgesForPoints(pointsThatDoesExist),
                        ResponseDetails.SUCCESS
                ), HttpStatusCode.valueOf(200)
        );
    }

    private <T> ResponseEntity<DetailedResponse<T>> getResponseOnModelNotFound(int id){
        return new ResponseEntity<>(
                new DetailedResponse<>(
                        (T) null,
                        new ResponseDetails("No Such Model", "No model with id: " + id + " exists", null)
                ),
                HttpStatusCode.valueOf(404)
        );
    }

    private <T> ResponseEntity<DetailedResponse<T>> getResponseOnRegistryMissing(){
        return new ResponseEntity<>(
                new DetailedResponse<>(
                    (T) null,
                    new ResponseDetails("Service Unavailable", "The internal model registry service is unavailable.", null)
                ),
                HttpStatusCode.valueOf(500)
        );
    }

}
