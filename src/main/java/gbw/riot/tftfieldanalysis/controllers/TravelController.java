package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.TravelBranchOptionDTO;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/travel", produces = "application/json")
public class TravelController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private DefaultResponseRegistryService responses;


    @Operation(summary = "Retrieves next options for the current travel context")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of options, unsorted."),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/next")
    public @ResponseBody ResponseEntity<DetailedResponse<List<TravelBranchOptionDTO>>>
    getBranchOptionsForStep(@PathVariable int modelId, @RequestParam String context)
    {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        //start from a set of culling parameters, get all possible points from here
        //then, select one, get dragged there, select new culling parameters.

        //returns an array of options based on the branching point (henceforth bp) of the last context entry
        //the points returned are all points the bp has edges to, culled by the parameters of the entry.

        return null;
    }

    @Operation(summary = "Retrieves the full travel context data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Each entry contains a list of branch options, where the first option is the \"branch origin point\""),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model"),
            @ApiResponse(responseCode="400", description = "Invalid context")
    })
    @GetMapping("/{modelId}/full")
    public @ResponseBody ResponseEntity<DetailedResponse<List<List<TravelBranchOptionDTO>>>>
    getFullTravelPath(@PathVariable int modelId, @RequestParam String context){
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(modelId);
        if(model == null){
            return responses.getResponseOnModelNotFound(modelId);
        }

        //returns the full traveled path based on the context.
        //first index of the nested array contains the bp.


        return null;
    }


}
