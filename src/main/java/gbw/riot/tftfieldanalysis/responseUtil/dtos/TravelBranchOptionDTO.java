package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.DataPoint;
import gbw.riot.tftfieldanalysis.core.Edge;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TravelBranchOptionDTO")
public record TravelBranchOptionDTO(DataPointDTO option, EdgeDTO edge) {
    public static TravelBranchOptionDTO of(DataPoint origin, DataPoint option, DataModel model){
        Edge edge = model.getEdgeBetween(origin,option);
        return new TravelBranchOptionDTO(
                DataPointDTO.of(option, model),
                EdgeDTO.of(edge)
        );
    }
}
