package gbw.riot.tftfieldanalysis.core.environmentloading;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;

import java.io.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class InternalBinaryDB {

    private static String root;
    static {
        File file = new File(".");
        try{
            root = file.getCanonicalPath();
        }catch (IOException e){
            System.err.println("SecretsService: Project Root Not Found");
            e.printStackTrace();
        }
    }

    public <T> ValueErrorTuple<T,Exception> retrieve(String path, String identifier, Class<T> clazz){
        return getManagedObjectInputStreamThenDo(
                path + "/" + identifier + ".bin",
                (ois) -> {
                    AtomicReference<T> duckTyped = new AtomicReference<>();
                    Object any = ois.readObject();
                    ValueErrorTuple.encapsulate(() -> {
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
    public <T> ValueErrorTuple<T,Exception> store(
            String path,
            T object,
            Function<T,String> identifierRetriever
    ) {
        if(object == null){
            return ValueErrorTuple.error(new NullPointerException("Why would you store a null object?"));
        }
        return getManagedObjectOutputStreamThenDo(
                path + "/" + identifierRetriever.apply(object) + ".bin",
                (oos) -> {
                    oos.writeObject(object);
                    return object;
                }
        );
    }

    private <T> ValueErrorTuple<T,Exception> getManagedObjectOutputStreamThenDo(
            String path,
            ValueErrorTuple.OneParameterTupleRetriever<
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

    private <T> ValueErrorTuple<T,Exception> getManagedObjectInputStreamThenDo(
            String path,
            ValueErrorTuple.OneParameterTupleRetriever<
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

    private <T> ValueErrorTuple<T,Exception> getManagedFileOutputStreamThenDo(
            String path,
            ValueErrorTuple.OneParameterTupleRetriever<T,FileOutputStream,Exception> thenDo
    ){
        final FileOutputStream[] fosArray = new FileOutputStream[1];
        Exception outputStreamCreationResult = ValueErrorTuple.encapsulate(
                () -> {
                    fosArray[0] = new FileOutputStream(path);
                }

        );
        if(outputStreamCreationResult != null){
            ValueErrorTuple.encapsulate(() -> fosArray[0].close());
            return ValueErrorTuple.error(outputStreamCreationResult);
        }
        FileOutputStream fos = fosArray[0];

        ValueErrorTuple<T,Exception> runResult = ValueErrorTuple.encapsulate(
                () -> thenDo.run(fos)
        );

        Exception inputStreamCloseResult = ValueErrorTuple.encapsulate(() -> {
            fos.close();
            fos.flush();
        });
        if(inputStreamCloseResult != null){
            return ValueErrorTuple.of(runResult.value(),inputStreamCloseResult);
        }

        return runResult;
    }

    private <T> ValueErrorTuple<T,Exception> getManagedFileInputStreamThenDo(
            String path,
            ValueErrorTuple.OneParameterTupleRetriever<T,FileInputStream,Exception> thenDo
    ){
        final FileInputStream[] fisArray = new FileInputStream[1];
        Exception outputStreamCreationResult = ValueErrorTuple.encapsulate(
                () -> {
                    fisArray[0] = new FileInputStream(path); //Well yes, because this solution is more flexible than "finally"
                }

        );
        if(outputStreamCreationResult != null){
            ValueErrorTuple.encapsulate(() -> fisArray[0].close()); //^ requires this line tho
            return ValueErrorTuple.error(outputStreamCreationResult);
        }
        FileInputStream fos = fisArray[0];

        ValueErrorTuple<T,Exception> runResult = ValueErrorTuple.encapsulate(
                () -> thenDo.run(fos)
        );

        Exception inputStreamCloseResult = ValueErrorTuple.encapsulate(fos::close);
        if(inputStreamCloseResult != null){
            return ValueErrorTuple.of(runResult.value(),inputStreamCloseResult);
        }

        return runResult;
    }
}
