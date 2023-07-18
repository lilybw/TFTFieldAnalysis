package gbw.riot.tftfieldanalysis.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbw.riot.tftfieldanalysis.core.travel.BranchEntry;
import gbw.riot.tftfieldanalysis.core.travel.Range;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.services.JsonService;
import jakarta.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    private JsonService jsonService;

    @GetMapping("/blank/{any}")
    public @ResponseBody Object testReturnWhatWasReceived(@RequestHeader Map<String, String> headers, @PathVariable String any, @RequestBody Object body){
        System.out.println("Headers recieved:");
        headers.forEach((key, value) -> System.out.println("Key: " + key + "Value: " + value));
        System.out.println("Path extension recieved: \t" + any);
        System.out.println("Body: " + body);
        return null;
    }

    @GetMapping("/could-json-work")
    public @ResponseBody BranchEntry[] testCouldJsonWorkForQueryString(@RequestParam String branchArray) throws JsonProcessingException {
        return jsonService.readValue(branchArray, BranchEntry[].class).value();
    }

}
