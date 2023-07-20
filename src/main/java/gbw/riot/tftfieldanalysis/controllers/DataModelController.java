package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Edge;
import gbw.riot.tftfieldanalysis.core.ModelMetaData;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.EdgeDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.ModelDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.ModelMetaDataDTO;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.EdgeSetProcessingService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;

import gbw.riot.tftfieldanalysis.services.PointCollectionProcessingService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/model", produces = "application/json")
public class DataModelController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private DefaultResponseRegistryService responses;

    @Autowired
    private EdgeSetProcessingService edgeService;

    @Autowired
    private PointCollectionProcessingService pointService;

    /**
     * @param modelId id of model to get data of
     * @return Retrieves ALL data associated with a given model. Do not automate. Only retrieve the data from a model you need.
     */
    @Operation(summary = "Retrieves ALL data associated with a given model. Do not automate. Only retrieve the data from a model you need.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}")
    public @ResponseBody ResponseEntity<DetailedResponse<ModelDTO>> getModel(@PathVariable int modelId)
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel modelFound = registry.retrieveModel(modelId);
        if(modelFound == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        ModelDTO.of(modelFound)
                ), HttpStatusCode.valueOf(200)
        );
    }


    /**
     * @return Retrieves all model ids currently in registry.
     */
    @Operation(summary = "Retrieves all model ids currently in registry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing")
    })
    @GetMapping("/all")
    public @ResponseBody ResponseEntity<DetailedResponse<List<Integer>>> getAllModels()
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        registry.getModelIds().stream().toList()
                ),
                HttpStatusCode.valueOf(200)
        );
    }


    /**
     * @return Create a new empty model.
     */
    @Operation(summary = "Create a new empty model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping("/create")
    public @ResponseBody ResponseEntity<DetailedResponse<ModelDTO>> createModel()
    {
        DataModel model = registry.createModel();
        return new ResponseEntity<>(
                DetailedResponse.of(
                        ModelDTO.of(model),
                        new ResponseDetails("Model Creation Successful", "", null)
                ),
                HttpStatusCode.valueOf(200)
        );
    }


    /**
     * @param modelId id of model to delete
     * @return Delete model permanently
     */
    @Operation(summary = "Delete model permanently.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @PostMapping("/{modelId}/delete")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> deleteModel(@PathVariable int modelId){
        if(registry == null) {
            return responses.getResponseOnRegistryMissing();
        }

        if(registry.retrieveModel(modelId) == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        if(registry.deleteModel(modelId)){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails("Successfully Deleted Model id: " + modelId, "", null)

                    ), HttpStatusCode.valueOf(200)
            );
        }
        return responses.getResponseOnModelNotFound(modelId);
    }


    /**
     * @param modelId to query
     * @param namespaces returned points have to be in
     * @param pointIds of specific points in question
     * @param tags returned points have to have
     * @return Query model DataPoints which conforms to provided parameters.
     */
    @Operation(summary = "Query model DataPoints which conforms to provided parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/points")
    public @ResponseBody ResponseEntity<DetailedResponse<List<DataPointDTO>>> getPointsInModel(
            @PathVariable int modelId,
            @RequestParam(required = false) String[] namespaces,
            @RequestParam(required = false) int[] pointIds,
            @RequestParam(required = false) String[] tags
    ){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        Set<DataPoint> toReturn = pointService.queryRequireAll(namespaces, pointIds, tags, model);

        return new ResponseEntity<>(
                DetailedResponse.of(
                        DataPointDTO.of(toReturn,model),
                        new ResponseDetails(
                                toReturn.size() == 0 ? "No Such Points Found" : "Points Retrieved Succesfully",
                                null,
                                null
                        )
                ), HttpStatusCode.valueOf(200)
        );
    }


    /**
     * @param modelId to retrieve namespaces from
     * @return String[n] | String[0] - All namespaces in model
     */
    @Operation(summary = "String[n] | String[0] - All namespaces in model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/namespaces")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getNamespacesOfModel(@PathVariable int modelId){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        model.getMetaData().dictionary().translateAll(model.getNamespaces())
                ), HttpStatusCode.valueOf(200)
        );
    }


    /**
     * @param points Points to get edge sets for
     * @param modelId Model to query
     * @param includedNamespaces What namespace to allow for resulting points - any if null or empty
     * @param includedTags What tags to all for resulting points - any if null or empty
     * @return Query edges for points, resulting edge lists are sorted based on occurrence value in descending order
     */
    @Operation(summary = "Query edges for points, resulting edge lists are sorted based on occurrence value in descending order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map of each point id provided as key and with a list of its edges as value"),
            @ApiResponse(responseCode = "206", description = "Partial success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model"),
            @ApiResponse(responseCode = "400", description = "No point ids provided"),
            @ApiResponse(responseCode = "400", description = "No such points in model")
    })
    @GetMapping("/{modelId}/edges")
    public @ResponseBody ResponseEntity<DetailedResponse<Map<Integer, List<EdgeDTO>>>>
    getEdgeSetForPoints(
            @RequestParam int[] points,
            @PathVariable int modelId,
            @RequestParam(required = false) String[] includedNamespaces,
            @RequestParam(required = false) String[] includedTags
    ){
        //preflight checks - fundamental
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        if(points == null || points.length == 0){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails(
                                    "Missing Ids",
                                    "Please provide valid point ids as query parameter \"...url...?points=x,y,z\"",
                                    null
                            )
                    ), HttpStatusCode.valueOf(400)
            );
        }

        //minor optimization.
        ArrayUtil.CollectionTuple<Integer> includedExcluded = ArrayUtil.resizeKeepRemainder(
                points, point -> model.getPointMap().get(point) != null
        );
        Collection<Integer> pointsThatDoesExist = includedExcluded.first();
        Collection<Integer> pointsThatDoesNotExist = includedExcluded.second();

        if(pointsThatDoesExist.size() == 0){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails(
                                    "No Valid Points Included",
                                    "Points: " + ArrayUtil.arrayJoinWith(points, ","),
                                    null
                            )
                    ), HttpStatusCode.valueOf(400)
            );
        }
        Map<Integer, Set<Edge>> edgesRaw = model.getEdgesForPoints(pointsThatDoesExist);
        //post fetch filter - not optimal. Each edge / point on insert should be load into more maps so this could be a look up
        edgesRaw = edgeService.reduceResultMapToInclusions(includedNamespaces, includedTags, model, edgesRaw);

        //Final edge sets found
        Map<Integer, List<EdgeDTO>> edges = EdgeDTO.of(edgesRaw);
        Map<Integer, List<EdgeDTO>> sortedEdges = new HashMap<>();
        for(int key : edges.keySet()){
            List<EdgeDTO> asList = new ArrayList<>(edges.get(key));
            asList.sort(Comparator.comparingLong(EdgeDTO::occurrence));
            Collections.reverse(asList);
            sortedEdges.put(
                    key,
                    asList
            );
        }

        if(!pointsThatDoesNotExist.isEmpty()){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            sortedEdges,
                            new ResponseDetails(
                                    "Partial Success",
                                    "Some points did not exist in model",
                                    pointsThatDoesNotExist.stream().map(String::valueOf).collect(Collectors.toList()
                                    )
                            )
                    ), HttpStatusCode.valueOf(206)
            );
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        sortedEdges
                ), HttpStatusCode.valueOf(200)
        );
    }

    /**
     * @param modelId
     * @return The metadata for the model
     */
    @Operation(summary = "The metadata for the model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Model metadata"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/metadata")
    public @ResponseBody ResponseEntity<DetailedResponse<ModelMetaDataDTO>>
    getMetadataOfModel(@PathVariable int modelId){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        ModelMetaDataDTO.of(model)
                ), HttpStatusCode.valueOf(200)
        );
    }

    /**
     * @return String[n] | String[0] - Retrieve all tags of any points in model as one List
     */
    @Operation(summary = "String[n] | String[0] - Retrieve all tags of any points in model as one List.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tags sorted on how many points had that tag, in descending order."),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/tags")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getTagsInModel(@PathVariable int modelId)
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        ModelMetaData metadata = model.getMetaData();
        List<Integer> sortedTags = metadata.pointsWithTagCount()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey).toList();

        List<String> translatedSortedTags = new ArrayList<>();
        sortedTags.forEach(tagAsInt -> translatedSortedTags.add(
                metadata.dictionary().translate(tagAsInt)
        ));

        return new ResponseEntity<>(
                DetailedResponse.success(
                        translatedSortedTags
                ), HttpStatusCode.valueOf(200)
        );
    }

}
