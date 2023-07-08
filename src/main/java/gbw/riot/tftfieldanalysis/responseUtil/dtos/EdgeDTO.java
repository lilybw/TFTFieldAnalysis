package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.Edge;

public record EdgeDTO(int pointA, int pointB, long occurrence) {
    public static EdgeDTO of(Edge edge){
        return new EdgeDTO(edge.pointA.getId(), edge.pointB.getId(), edge.value);
    }
}
