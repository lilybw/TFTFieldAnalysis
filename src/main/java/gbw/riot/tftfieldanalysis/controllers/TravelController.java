package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.TravelBranchOptionDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.TravelContextSyntaxDeclaration;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.TravelContextParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/travel")
public class TravelController {
    //asset "streaming" yay!

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private DefaultResponseRegistryService responses;

    @Autowired
    private TravelContextParser parser;

    @Operation(summary = "Retrieves next options for the current travel context")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of options, unsorted."),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "404", description = "No such model")
    })
    @GetMapping("/{modelId}/next")
    public @ResponseBody ResponseEntity<DetailedResponse<TravelBranchOptionDTO[]>>
    travelModel(@PathVariable int modelId, @RequestParam String context)
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
    public @ResponseBody ResponseEntity<DetailedResponse<TravelBranchOptionDTO[][]>>
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

    @Operation(summary = "Retrieves documentation regarding the whole Travel Context ordeal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of options, unsorted.")
    })
    @GetMapping("/contextSyntaxDeclaration")
    public @ResponseBody ResponseEntity<DetailedResponse<TravelContextSyntaxDeclaration>>
    getSyntaxDeclaration(){
        return new ResponseEntity<>(
                DetailedResponse.success(
                    new TravelContextSyntaxDeclaration(
                            parser.entryEnd,
                            parser.entrySubdivision,
                            parser.entryMemberSubdivision,
                            "Each entry member is a \""+parser.entryMemberSubdivision +"\" separated array.\n" +
                                    "The members themselves are separated by: \""+parser.entrySubdivision +"\".\n" +
                                    "Each branch entry is separated by: \""+parser.entryEnd +"\""
                    )
                ), HttpStatusCode.valueOf(200)
        );
    }


}
