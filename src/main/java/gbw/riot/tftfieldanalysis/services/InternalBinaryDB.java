package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.BinaryDB;
import gbw.riot.tftfieldanalysis.core.ValErr;
import gbw.riot.tftfieldanalysis.core.environmentloading.FileUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Service
public class InternalBinaryDB implements BinaryDB {

    private static String root;
    //Super Secret Proprietary File Type = binary stored java object
    private final String SSPFT = ".bsjo";
    static {
        File file = new File(".");
        try{
            root = file.getCanonicalPath();
            root += "/src/main/resources/dynamic";
        }catch (IOException e){
            System.err.println("SecretsService: Project Root Not Found");
            e.printStackTrace();
        }
    }
    private record OnShutdownEntry(OnShutdownStore func, Priority prio, String identifier){}

    private final List<OnShutdownEntry> onShutdownFunctions = new ArrayList<>();

    public InternalBinaryDB(){
        Runtime.getRuntime().addShutdownHook(
                new Thread(this::onShutdown)
        );
    }

    private void onShutdown(){
        System.out.println("Running DB shutdown operations.");
        for(OnShutdownEntry entry : onShutdownFunctions){
            Exception error = entry.func().run(this);
            if(error != null){
                System.err.println(error.getMessage());

            }
        }
    }

    public boolean appendOperationOnShutdown(
            OnShutdownStore func,
            BinaryDB.Priority priority,
            String identifier
    ){
        boolean toReturn = onShutdownFunctions.add(
                new OnShutdownEntry(func, priority, identifier)
        );
        onShutdownFunctions.sort((e1,e2) ->
             Integer.compare(e2.prio().intVal,e1.prio().intVal)
        );

        return toReturn;
    }

    public <T> ValErr<Collection<T>,List<Exception>> retrieveCollection(
            String directory, Class<T> clazz
    ){
        List<File> filesInDir = FileUtil.getFiles(root + "/" + directory, SSPFT);
        Collection<T> toReturn = new ArrayList<>();
        List<Exception> errors = new ArrayList<>();

        for(File file : filesInDir){
            ValErr<T,Exception> result = retrieve(
                    directory,
                    FileUtil.removeExtension(file.getName()),
                    clazz
            );
            if(result.hasError()) errors.add(result.error());
            if(result.value() != null) toReturn.add(result.value());
        }

        return ValErr.of(
                toReturn,
                errors.isEmpty() ? null : errors
        );
    }

    @SuppressWarnings("unchecked")
    public <T> ValErr<T,Exception> retrieve(String path, String identifier, Class<T> clazz){
        return getManagedObjectInputStreamThenDo(
                root + "/" + path + "/" + identifier + SSPFT,
                (ois) -> {
                    AtomicReference<T> duckTyped = new AtomicReference<>();
                    Object any = ois.readObject();
                    ValErr.encapsulate(() -> {
                        duckTyped.set((T) any);
                    });
                    return duckTyped.get();
                }
        );
    }

    /**
     *
     * @param path to directory where the bin file should be stored.
     * @param object to store
     * @param identifierRetriever how to identify this object. This value is needed to retrieve it again.
     * @param <T> type of to-be-stored object
     * @return [T | null, Exception | null]

     */
    public <T> ValErr<T,Exception> store(
            String path,
            T object,
            Function<T,String> identifierRetriever
    ) {
        if(object == null){
            return ValErr.error(new NullPointerException("Why would you store a null object?"));
        }
        return getManagedObjectOutputStreamThenDo(
                root + "/" + path + "/" + identifierRetriever.apply(object) + SSPFT,
                (oos) -> {
                    oos.writeObject(object);
                    return object;
                }
        );
    }

    private <T> ValErr<T,Exception> getManagedObjectOutputStreamThenDo(
            String path,
            ValErr.OneParameterTupleRetriever<
                    T,
                    ObjectOutputStream,
                    Exception> thenDo
    ){
        return getManagedFileOutputStreamThenDo(
                path,
                (stream) -> {
                    ObjectOutputStream oos = new ObjectOutputStream(stream);
                    T result = thenDo.run(oos);
                    oos.close();
                    return result;
                }

       );
    }

    /**
     * If the ObjectInputStream cannot be retrieved, the function is not run.
     * Gets the stream to the path, runs the function then closes the stream
     * @param path complete path of file - with extension
     * @param thenDo function to run using the ObjectInputStream
     * @param <T> type param
     * @return any error.
     */
    private <T> ValErr<T,Exception> getManagedObjectInputStreamThenDo(
            String path,
            ValErr.OneParameterTupleRetriever<
                    T,
                    ObjectInputStream,
                    Exception> thenDo
    ){
        return getManagedFileInputStreamThenDo(
                path,
                (stream) -> {
                    ObjectInputStream oos = new ObjectInputStream(stream);
                    T result = thenDo.run(oos);
                    oos.close();
                    return result;
                }
        );
    }

    private <T> ValErr<T,Exception> getManagedFileOutputStreamThenDo(
            String path,
            ValErr.OneParameterTupleRetriever<T,FileOutputStream,Exception> thenDo
    ){
        FileUtil.createIfNotExists(path);
        final FileOutputStream[] fosArray = new FileOutputStream[1];
        Exception outputStreamCreationResult = ValErr.encapsulate(
                () -> {
                    fosArray[0] = new FileOutputStream(path);
                }

        );
        if(outputStreamCreationResult != null){
            ValErr.encapsulate(() -> fosArray[0].close());
            return ValErr.error(outputStreamCreationResult);
        }
        FileOutputStream fos = fosArray[0];

        ValErr<T,Exception> runResult = ValErr.encapsulate(
                () -> thenDo.run(fos)
        );

        Exception inputStreamCloseResult = ValErr.encapsulate(() -> {
            fos.close();
            fos.flush();
        });
        if(inputStreamCloseResult != null){
            return ValErr.of(runResult.value(),inputStreamCloseResult);
        }

        return runResult;
    }

    /**
     * If the retrieval of the input stream fail, the function is not run
     * Opens a stream to the path, runs the function and closes the stream
     * @param path full path of file including extension
     * @param thenDo function to run using said stream
     * @param <T> type param
     * @return Any error and the result of the provided function
     */
    private <T> ValErr<T,Exception> getManagedFileInputStreamThenDo(
            String path,
            ValErr.OneParameterTupleRetriever<T,FileInputStream,Exception> thenDo
    ){
        final FileInputStream[] fisArray = new FileInputStream[1];
        Exception outputStreamCreationResult = ValErr.encapsulate(
                () -> {
                    fisArray[0] = new FileInputStream(path); //Well yes, because this solution is more flexible than "finally"
                }

        );
        if(outputStreamCreationResult != null){
            ValErr.encapsulate(() -> fisArray[0].close()); //^ requires this line tho
            return ValErr.error(outputStreamCreationResult);
        }
        FileInputStream fos = fisArray[0];

        ValErr<T,Exception> runResult = ValErr.encapsulate(
                () -> thenDo.run(fos)
        );

        Exception inputStreamCloseResult = ValErr.encapsulate(fos::close);
        if(inputStreamCloseResult != null){
            return ValErr.of(runResult.value(),inputStreamCloseResult);
        }

        return runResult;
    }
}
