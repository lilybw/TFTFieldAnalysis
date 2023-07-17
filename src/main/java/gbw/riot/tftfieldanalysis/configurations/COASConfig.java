package gbw.riot.tftfieldanalysis.configurations;

import gbw.riot.tftfieldanalysis.TftFieldAnalysisApplication;
import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * CustomOpenApiConfig
 * <br>
 * NAMING CONVENTION (else this'll be an unmaintainable mess) <br>
 * Pascal case <br>
 * Any type shall be referred to as its complex version (if applicable) <br>
 * Each type is written in order of nesting: Ta<\Na<\Ka<\Va>>>> => TaNaKaVa <br>
 * Any singular type is prefixed with DR (DetailedResponse): String => DRString <br>
 * Multi-dimensional collections indicate dimension size as suffix: String[][] => Array2DString <br>
 */
@Configuration
public class COASConfig {

    @Schema(description = "Details response of only details.")
    public static class DetailsOnly extends DetailedResponse<Void> {}

    @Schema(description = "Detailed response with Map of keys: Integer and values: List of EdgeDTO")
    public static class MapIntegerListEdgeDTO extends DetailedResponse<Map<Integer,List<EdgeDTO>>> {}

    @Schema(description = "Detailed response with a list of strings.")
    public static class ListString extends DetailedResponse<List<String>>{}

    @Schema(description = "Detailed response with a list of EdgeDTO objects.")
    public static class ListEdgeDTO extends DetailedResponse<List<EdgeDTO>>{}

    @Schema(description = "Detailed response with a list of EdgeDTO objects.")
    public static class ListDataPointDTO extends DetailedResponse<List<DataPointDTO>>{}

    @Schema(description = "Detailed response with a ModelDTO object.")
    public static class DRModelDTO extends DetailedResponse<ModelDTO>{}

    @Schema(description = "Detailed response with a list of integers.")
    public static class ListInteger extends DetailedResponse<List<Integer>>{}

    @Schema(description = "Detailed response with a string.")
    public static class DRString extends DetailedResponse<String>{}

    @Schema(description = "Detailed response of the metadata for a model.")
    public static class DRModelMetadataDTO extends DetailedResponse<ModelMetaDataDTO>{}

    @Schema(description = "Detailed response of an integer.")
    public static class DRInteger extends DetailedResponse<Integer>{}

    @Schema(description = "Detailed response of an array of travel branch options.")
    public static class ListTravelBranchOptionDTO extends DetailedResponse<List<TravelBranchOptionDTO>>{}

    @Schema(description = "Detailed response of a 2D array of travel branch options.")
    public static class List2DTravelBranchOptionDTO extends DetailedResponse<List<List<TravelBranchOptionDTO>>>{}

    @Schema(description = "Detailed response of the travel context syntax declaration.")
    public static class DRTravelContextSyntaxDeclaration extends DetailedResponse<TravelContextSyntaxDeclaration>{}


}
