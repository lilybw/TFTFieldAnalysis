package gbw.riot.tftfieldanalysis.controllers;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.services.WebServiceMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceMetadataController {

    @Autowired
    private WebServiceMetadataService serviceData;

    @GetMapping("/version")
    public @ResponseBody ResponseEntity<DetailedResponse<String>> getVersion()
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

}
