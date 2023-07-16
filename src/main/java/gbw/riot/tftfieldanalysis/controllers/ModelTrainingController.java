package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.ServerLocations;
import gbw.riot.tftfieldanalysis.core.SummonerDTO;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.services.DataRetrievalService;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelTrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/train", produces = "application/json")
public class ModelTrainingController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private ModelTrainingService trainer;

    @Autowired
    private DefaultResponseRegistryService responses;

    @Autowired
    private DataRetrievalService dataRetrievalService;

    @Operation(summary = "Static asset retrieval: Get TFT Server Targets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "String array of valid TFT servers.")
    })
    @GetMapping("/serverTargets")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getValidTFTServerTargets()
    {
        ModelTrainingService.TrainingConfiguration.ServerTargets[] values = ArrayUtil.removeTail(ModelTrainingService.TrainingConfiguration.ServerTargets.values(),1);
        List<String> asStringList = new ArrayList<>(values.length);
        for(ModelTrainingService.TrainingConfiguration.ServerTargets value : values){
            asStringList.add(value.target);
        }
        return new ResponseEntity<>(
                DetailedResponse.success(
                        asStringList
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Static asset retrieval: Get RIOT Account Server Targets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "String array of valid account servers.")
    })
    @GetMapping("/serverLocations")
    public @ResponseBody ResponseEntity<DetailedResponse<List<String>>>
    getAccountServerLocations()
    {
        ServerLocations[] values = ArrayUtil.removeTail(ServerLocations.values(),1);
        List<String> asStringList = new ArrayList<>(values.length);
        for(ServerLocations value : values){
            asStringList.add(value.domain);
        }
        return new ResponseEntity<>(
                DetailedResponse.success(
                        asStringList
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Cross confirmation: Validate account, returns player puuid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player puuid"),
            @ApiResponse(responseCode = "404", description = "Invalid account server target"),
            @ApiResponse(responseCode = "400", description = "Unable to locate/access account")
    })
    @GetMapping("/validate/{ign}/server/{server}")
    public @ResponseBody ResponseEntity<DetailedResponse<String>>
    validatePlayerIGN(@PathVariable String ign, @PathVariable String server){
        ServerLocations location = ServerLocations.byDomain(server);

        if(location == ServerLocations.ERR_UNKNOWN){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails(
                                    "Invalid Server Location", "Valid location are: " + ArrayUtil.arrayJoinWith(ServerLocations.values(), ","), null
                            )
                    ), HttpStatusCode.valueOf(404)
            );
        }

        ValueErrorTuple<SummonerDTO, Exception> result = dataRetrievalService.getAccount(ign, location);
        if(result.error() != null){
            return new ResponseEntity<>(
                    DetailedResponse.details(
                            new ResponseDetails(result.error().getMessage(),"Description intentionally cut short.", null)
                    ), HttpStatusCode.valueOf(400)
            );
        }

        return new ResponseEntity<>(
                DetailedResponse.success(
                        result.value().puuid()
                ), HttpStatusCode.valueOf(200)
        );
    }

    @Operation(summary = "Train model, response withheld until completion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Model id"),
            @ApiResponse(responseCode = "404", description = "Unknown model"),
            @ApiResponse(responseCode = "500", description = "Internal model registry missing"),
            @ApiResponse(responseCode = "501", description = "Error encountered while training, details.notes contains what matchIds were evaluated"),
            @ApiResponse(responseCode = "400", description = "Error encountered before training start"),
            @ApiResponse(responseCode = "400", description = "Missing PUUID query parameter")
    })
    @PostMapping("/{id}")
    public @ResponseBody ResponseEntity<DetailedResponse<Integer>> trainModel(
            @PathVariable int id,
            @RequestParam String puuid,
            @RequestBody(required = false) ModelTrainingService.TrainingConfiguration config
    ) {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }
        if(puuid == null || puuid.isEmpty()){
            return responses.getResponseOnMissingPUUID();
        }

        ValueErrorTuple<Set<String>,Exception> result = trainer.run(model, puuid, config);
        if(result.error() != null && result.value() != null){
            return new ResponseEntity<>(
                    DetailedResponse.of(
                            model.getMetaData().modelId(),
                            new ResponseDetails(
                                    "Issue Encountered While Training",
                                    result.error().getMessage(),
                                    List.of("Matches evaluated: " +
                                            ArrayUtil.arrayJoinWith(result.value().toArray(new String[0]), ", ")
                                    )
                            )
                    ), HttpStatusCode.valueOf(501)
            );
        }
        if(result.error() != null){
            return new ResponseEntity<>(
                    DetailedResponse.of(
                            model.getMetaData().modelId(),
                            new ResponseDetails(
                                    "Issue Encountered Before Training",
                                    result.error().getMessage(),
                                    null
                            )
                    ), HttpStatusCode.valueOf(400)
            );
        }

        return new ResponseEntity<>(
                DetailedResponse.of(
                        model.getMetaData().modelId(),
                        new ResponseDetails("Training Complete", "Matches evaluated: " + ArrayUtil.arrayJoinWith(result.value().toArray(new String[0]), ", "),
                                List.of("Model: " + id, "maxMatchCount: " + config.maxMatchCount, "patch: " + config.patch)
                        )
                ), HttpStatusCode.valueOf(200)
        );
    }

}
