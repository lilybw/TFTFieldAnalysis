package gbw.riot.tftfieldanalysis.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class JsonService {

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> ValueErrorTuple<T,Exception> readValue(String string, Class<T> clazz) {
        return ValueErrorTuple.encapsulate(
                () -> mapper.readValue(string,clazz)
        );
    }

    public <T> ValueErrorTuple<Collection<T>,Exception> readCollection(String string, Class<T> clazz) {
        return ValueErrorTuple.encapsulate(
                () -> mapper.readValue(string, new TypeReference<Collection<T>>(){})
        );
    }

    public <T> ValueErrorTuple<List<T>,Exception> readList(String string, Class<T> clazz) {
        return ValueErrorTuple.encapsulate(
                () -> Arrays.asList(mapper.readValue(string, clazz))
        );
    }
}
