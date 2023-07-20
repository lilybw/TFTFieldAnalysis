package gbw.riot.tftfieldanalysis.core;

import gbw.riot.tftfieldanalysis.services.InternalBinaryDB;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface BinaryDB {

    @FunctionalInterface
    interface OnShutdownStore {
        /**
         * @param db instance
         * @return Describing exception on error, else null
         */
        Exception run(InternalBinaryDB db);
    }
    enum Priority {
        LOW(1000),
        MID(100),
        HIGH(10),
        SYSTEM_CRITICAL(1);
        public int intVal;
        Priority(int intVal){
            this.intVal = intVal;
        }
    }
    enum KnownLocation {
        MODELS("models");
        public final String directory;
        KnownLocation(String dir){
            this.directory = dir;
        }
    }

    /**
     * @param path sub-path from DB specified directory - without prefix slash
     * @param identifier of file - not including file type, which is always .bin regardless
     * @param clazz What class to expect the resulting object to be.
     * @param <T> type param.
     * @return The object, null on error, and the error.
     */
    <T> ValueErrorTuple<T, Exception> retrieve(String path, String identifier, Class<T> clazz);

    /**
     * @param clazz expected type of collection
     * @param directory sub-directory to load - without prefix slash
     * @param <T> type param
     * @return a collection of any instanced stored in any file in that directory of said type. And any error.
     */
    <T> ValueErrorTuple<Collection<T>, List<Exception>> retrieveCollection(String directory, Class<T> clazz);

    /**
     * @param path sub-path from DB specified directory
     * @param object to store
     * @param identifierRetriever how to retrieve a unique id for any object of this type
     * @param <T> type param
     * @return The object and any error that arose while storing it
     */
    <T> ValueErrorTuple<T, Exception> store(String path, T object, Function<T, String> identifierRetriever);

    /**
     *
     * @param func Function to run
     * @param priority Additional info - how important is this?
     * @param identifier Additional info, letting the operation be identified. Should be understandable to humans.
     * @return whether append was successful.
     */
    boolean appendOperationOnShutdown(OnShutdownStore func, Priority priority, String identifier);
}
