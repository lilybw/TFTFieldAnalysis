package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.DataModel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Service
public class ModelRegistryService {

    private final HashMap<Integer, DataModel> registry = new HashMap<>();

    public DataModel createModel()
    {
        DataModel model = new DataModel();
        registry.put(model.getMetaData().modelId(), model);
        return model;
    }

    public void registerModels(Collection<DataModel> models){
        models.forEach(this::registerModel);
    }

    /**
     * Returns wether or not the model was already registered.
     */
    public boolean registerModel(DataModel model){
        if(registry.get(model.getMetaData().modelId()) != null){
            return false;
        }
        registry.put(model.getMetaData().modelId(), model);
        return true;
    }

    public HashMap<Integer,DataModel> __getRegistry(){
        return registry;
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
