package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.ResponseDetails;
import gbw.riot.tftfieldanalysis.services.DefaultResponseRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.ModelTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/train")
public class MdelTrainingController {

    @Autowired
    private ModelRegistryService registry;

    @Autowired
    private ModelTrainingService trainer;

    @Autowired
    private DefaultResponseRegistryService responses;

    @PostMapping("/{id}")
    public @ResponseBody
    ResponseEntity<DetailedResponse<Integer>> trainModel(
            @PathVariable int id,
            @RequestParam(required = false) String puuid,
            @RequestBody(required = false) ModelTrainingService.TrainingConfiguration config
    ) {
        if(registry == null){
            return responses.getResponseOnRegistryMissing();
        }
        DataModel model = registry.retrieveModel(id);
        if(model == null){
            return responses.getResponseOnModelNotFound(id);
        }
        puuid = puuid == null ? "PbehKkjRrNApiTrB_Q5IH5a0EAozAHNRFdd_ObZQW1c4Pt3ZL22A-gt1kFPOaxpERXRCPSQWpy7kNQ" : puuid;
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
                    ), HttpStatusCode.valueOf(500)
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
