package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.services.WebServiceMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceMetadataController {

    @Autowired
    private WebServiceMetadataService serviceData;

    @Operation(summary = "Retrieve current web service version.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Web service version."),
            @ApiResponse(responseCode = "500", description = "Internal error, unable to retrieve value.")
    })
    @GetMapping("/version")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> getServiceVersion()
    {
        ValueErrorTuple<String,Exception> result = serviceData.getVersion();
        if(result.error() != null){
            return new ResponseEntity<>(
                    DetailedResponse.success(
                            result.error().getMessage()
                    ), HttpStatusCode.valueOf(500)
            );
        }
        return new ResponseEntity<>(
                DetailedResponse.success(
                        result.value()
                ), HttpStatusCode.valueOf(200)
        );
    }

    @GetMapping("/")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> pingService()
    {
        return new ResponseEntity<>(
                DetailedResponse.success(
                        "pong"
                ), HttpStatusCode.valueOf(200)
        );
    }

}
