package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class ModelRegistryService {

    private final HashMap<Integer, DataModel> registry = new HashMap<>();

    public DataModel createModel()
    {
        DataModel model = new DataModel();
        registry.put(model.hashCode(), model);
        return model;
    }

    /**
     * Returns wether or not the model was already registered.
     */
    public boolean registerModel(DataModel model){
        if(registry.get(model.hashCode()) != null){
            return false;
        }
        registry.put(model.hashCode(), model);
        return true;
    }

    public boolean deleteModel(int id) {
        if(registry.get(id) == null){
            return false;
        }
        registry.remove(id);
        return true;
    }

    public Set<Integer> getModelIds(){
        return registry.keySet();
    }

    public DataModel retrieveModel(int id){
        return registry.get(id);
    }

}
