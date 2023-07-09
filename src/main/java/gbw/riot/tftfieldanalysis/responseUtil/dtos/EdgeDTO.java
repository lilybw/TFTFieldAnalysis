package gbw.riot.tftfieldanalysis.responseUtil.dtos;

import gbw.riot.tftfieldanalysis.core.Edge;

import java.util.*;

public record EdgeDTO(int pointA, int pointB, long occurrence) {
    public static EdgeDTO of(Edge edge){
        return new EdgeDTO(edge.pointA.getId(), edge.pointB.getId(), edge.value);
    }
    public static Set<EdgeDTO> of(Collection<Edge> list){
        Set<EdgeDTO> toReturn = new HashSet<>(list.size());
        for(Edge edge : list){
            toReturn.add(of(edge));
        }
        return toReturn;
    }
    public static <T> Map<T, Set<EdgeDTO>> of(Map<T,Set<Edge>> map){
        Map<T, Set<EdgeDTO>> toReturn = new HashMap<>();
        for(T key : map.keySet()){
            toReturn.put(key, of(map.get(key)));
        }
        return toReturn;
    }

}
