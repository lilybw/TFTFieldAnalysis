package gbw.riot.tftfieldanalysis;

import gbw.riot.tftfieldanalysis.core.BinaryDB;
import gbw.riot.tftfieldanalysis.core.DataModel;
import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.services.ModelRegistryService;
import gbw.riot.tftfieldanalysis.services.SecretsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

@SpringBootApplication
public class TftFieldAnalysisApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TftFieldAnalysisApplication.class, args);
        BinaryDB db = context.getBean(BinaryDB.class);
        ModelRegistryService registry = context.getBean(ModelRegistryService.class);
        SecretsService secrets = context.getBean(SecretsService.class);

        ValueErrorTuple<String,Exception> shouldStoreResult = secrets.getConfigurable("Store-Models-On-Shutdown");
        if(!shouldStoreResult.hasError() && shouldStoreResult.value().equalsIgnoreCase("true")){
            appendShutdownHooks(db,registry);
        }

        ValueErrorTuple<Collection<DataModel>,List<Exception>> persistingResult =
                db.retrieveCollection(BinaryDB.KnownLocation.MODELS.directory, DataModel.class);

        registry.registerModels(persistingResult.value());
        if(persistingResult.hasError()){
            for(Exception e : persistingResult.error()){
                System.err.println(e.getMessage());
            }
        }
    }

    private static void appendShutdownHooks(BinaryDB db, ModelRegistryService registry){
        db.appendOperationOnShutdown(
            instance -> {
                    List<Exception> issues = new ArrayList<>();
                    for(Map.Entry<Integer, DataModel> entry : registry.__getRegistry().entrySet()) {
                        if (entry.getValue() == null) continue;

                        ValueErrorTuple<DataModel, Exception> result = instance.store(
                                BinaryDB.KnownLocation.MODELS.directory,
                                entry.getValue(),
                                e -> entry.getKey() + ""
                        );
                        if (result.hasError()) {
                            issues.add(result.error());
                        }
                    }
                    if(!issues.isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for(Exception e : issues){
                            sb.append(e.getMessage()).append("\n");
                        }
                        return new Exception(sb.toString());
                    }
                    return null;
                },
            BinaryDB.Priority.HIGH, "Storing models on shutdown.");
    }

}
