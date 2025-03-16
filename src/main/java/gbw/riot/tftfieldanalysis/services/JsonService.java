package gbw.riot.tftfieldanalysis.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gbw.riot.tftfieldanalysis.core.ValErr;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class JsonService {

    private final ObjectMapper mapper = new ObjectMapper();

    public <T> ValErr<T,Exception> readValue(String string, Class<T> clazz) {
        return ValErr.encapsulate(
                () -> mapper.readValue(string,clazz)
        );
    }
    
    public <T> ValErr<Collection<T>,Exception> readCollection(String string, Class<T> clazz) {
        return ValErr.encapsulate(
                () -> mapper.readValue(string, new TypeReference<Collection<T>>(){})
        );
    }

    public <T> ValErr<List<T>,Exception> readList(String string, Class<T> clazz) {
        return ValErr.encapsulate(
                () -> Arrays.asList(mapper.readValue(string, clazz))
        );
    }
}
