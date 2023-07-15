package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.TravelContextSyntaxDeclaration;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.TravelContextParser;
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

    @GetMapping("/{modelId}/next")
    public @ResponseBody ResponseEntity<DetailedResponse<DataPoint[]>>
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

    @GetMapping("/{modelId}/full")
    public @ResponseBody ResponseEntity<DetailedResponse<DataPoint[][]>>
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
