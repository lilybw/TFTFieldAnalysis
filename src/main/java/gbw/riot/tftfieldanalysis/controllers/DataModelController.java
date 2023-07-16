package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.configurations.COASConfig;
import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.compressors.Dictionary;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.EdgeDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.ModelDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.ModelMetaDataDTO;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/model")
public class DataModelController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private DefaultResponseRegistryService responses;

    @Operation(summary = "Retrieves ALL data associated with a given model. Do not automate. Only retrieve the data from a model you need.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<DetailedResponse<ModelDTO>> getModel(@PathVariable int id)
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel modelFound = registry.retrieveModel(id);
        if(modelFound == null){
            return responses.getResponseOnModelNotFound(id);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        ModelDTO.of(modelFound)
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Retrieves all model ids currently in registry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing")
    })
    @GetMapping("/all")
    public @ResponseBody ResponseEntity<DetailedResponse<List<Integer>>> getAllModelIds()
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

    @Operation(summary = "Create a new empty model")
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

    @Operation(summary = "Remove model permanently.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @PostMapping("/{id}/delete")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> deleteModel(@PathVariable int id){
        if(registry == null) {
            return responses.getResponseOnRegistryMissing();
        }

        if(registry.retrieveModel(id) == null){
            return responses.getResponseOnModelNotFound(id);
        }

        if(registry.deleteModel(id)){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails("Successfully Deleted Model id: " + id, "", null)

                    ), HttpStatusCode.valueOf(200)
            );
        }
        return responses.getResponseOnModelNotFound(id);
    }

    @Operation(summary = "Query model DataPoints which conforms to provided parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model"),
            @ApiResponse(responseCode = "418", description = "Api usage error.")
    })
    @GetMapping("/{id}/points")
    public @ResponseBody ResponseEntity<DetailedResponse<List<DataPointDTO>>> getPoints(
            @PathVariable int id,
            @RequestParam(required = false) String namespace,
            @RequestParam(required = false) int[] pointIds,
            @RequestParam(required = false) String[] tags
    ){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }

        int computedResolution = 0;
        computedResolution += namespace == null || namespace.length() == 0 ? 0 : 1;
        computedResolution += pointIds == null || pointIds.length == 0 ? 0 : 2;
        computedResolution += tags == null || tags.length == 0 ? 0 : 4;

        Dictionary<String> dictionary = model.getMetaData().dictionary();
        int translatedNamespace = dictionary.reverseTranslate(namespace);
        int[] translatedTags;

        Set<DataPoint> toReturn;

        switch (computedResolution) {
            case 0 -> //neither namespace nor pointIds declared
                toReturn = model.getAllPoints();
            case 1 -> //namespace only
                toReturn = model.getPointsInNamespace(namespace);
            case 2 -> //pointIds only
                toReturn = model.getSpecificDataPoints(pointIds);
            case 3 -> //points in namespace of ids pointIds
                toReturn = ArrayUtil.resize(
                                        model.getPointsInNamespace(namespace),
                                        point -> ArrayUtil.contains(pointIds, point.getId())
                                );
            case 4 -> {
                toReturn = model.getPointsWithTags(tags);
            }
            case 5 -> {//points in namespace x with tags x,y,z
                translatedTags = dictionary.reverseTranslateAll(tags);
                toReturn = ArrayUtil.resize(
                        model.getPointsInNamespace(namespace),
                        point -> ArrayUtil.containsAnyOf(translatedTags, point.getTags())
                );
            }
            case 6 -> {  //points with ids x,y,z and tags x,y,z
                translatedTags = dictionary.reverseTranslateAll(tags);
                toReturn = ArrayUtil.resize(
                        model.getSpecificDataPoints(pointIds),
                        point -> ArrayUtil.containsAnyOf(translatedTags, point.getTags())
                );
            }
            case 7 -> { //points with ids x,y,z and tags x,y,z within namespace x
                Set<DataPoint> withTags = model.getPointsWithTags(tags);
                Set<DataPoint> withIds = model.getSpecificDataPoints(pointIds);
                translatedTags = dictionary.reverseTranslateAll(tags);
                if(withIds.size() <= withTags.size()){
                    toReturn = withIds.stream()
                            .filter(point -> point.getNamespace() == translatedNamespace)
                            .filter(point -> ArrayUtil.containsAnyOf(translatedTags,point.getTags()))
                            .collect(Collectors.toSet());
                }else {
                    toReturn = withTags.stream()
                            .filter(point -> point.getNamespace() == translatedNamespace)
                            .filter(point -> ArrayUtil.isAnyOf(point.getId(), pointIds))
                            .collect(Collectors.toSet());
                }
            }
            default -> {
                return new ResponseEntity<>(
                        DetailedResponse.unknownError(),
                        HttpStatusCode.valueOf(500)
                );
            }
        }
        if(toReturn == null){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails("Unknown API usage error", "Somehow, you've managed to end up here. Well done.", null)
                    ), HttpStatusCode.valueOf(400)
            );
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        DataPointDTO.of(toReturn,model)
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Retrieves all namespaces in model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{id}/namespaces")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getNamespaces(@PathVariable int id){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        model.getMetaData().dictionary().translateAll(model.getNamespaces())
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Query edges for points, resulting edge lists are sorted based on occurrence value in descending order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map of each point id provided as key and with a list of its edges as value"),
            @ApiResponse(responseCode = "206", description = "Partial success"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model"),
            @ApiResponse(responseCode = "404", description = "No point ids provided"),
            @ApiResponse(responseCode = "400", description = "No such points in model")
    })
    @GetMapping("/{id}/edges")
    public @ResponseBody ResponseEntity<DetailedResponse<Map<Integer, List<EdgeDTO>>>>
    getEdgeSets(
            @RequestParam int[] points, @PathVariable int id
    ){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }

        if(points == null || points.length == 0){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails(
                                    "Missing Ids",
                                    "Please provide valid point ids as query parameter \"...url...?points=x,y,z\"",
                                    null
                            )
                    ), HttpStatusCode.valueOf(404)
            );
        }

        int[] pointsThatDoesNotExist = new int[0];
        int[] pointsThatDoesExist = ArrayUtil.resize(points, point -> model.getPointMap().get(point) != null);
        boolean invalidPointsIncluded = false;
        if(pointsThatDoesExist.length != points.length){
            pointsThatDoesNotExist = ArrayUtil.resize(points, point -> model.getPointMap().get(point) == null);
            invalidPointsIncluded = true;
        }

        if(pointsThatDoesExist.length == 0){
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

        Map<Integer, List<EdgeDTO>> edges = EdgeDTO.of(model.getEdgesForPoints(pointsThatDoesExist));
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

        if(invalidPointsIncluded){
            return new ResponseEntity<>(
                    new DetailedResponse<>(
                            sortedEdges,
                            new ResponseDetails(
                                    "Partial Success", "Some points did not exist in model", ArrayUtil.fromIntArrayToStringList(pointsThatDoesNotExist)
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

    @Operation(summary = "Get metadata for model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Model metadata"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{id}/metadata")
    public @ResponseBody ResponseEntity<DetailedResponse<ModelMetaDataDTO>>
    getModelMetadata(@PathVariable int id){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }

        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        ModelMetaDataDTO.of(model)
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Retrives all tags for all points in model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tags sorted on how many points had that tag, in descending order."),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{id}/tags")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getModelTags(@PathVariable int id)
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }

        DataModel.ModelMetaData metadata = model.getMetaData();
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
