package gbw.riot.tftfieldanalysis.core.environmentloading;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<File> getFiles(String folderPath){
        File folder = new File(folderPath);
        List<File> toReturn = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !file.getName().isEmpty()) {
                        toReturn.add(file);
                    }
                }
            }
        }
        return toReturn;
    }

    public static List<File> getFiles(String folderPath, String extension){
        List<File> files = getFiles(folderPath);
        if(files.isEmpty()) return files;

        return files.stream().filter(f -> {
            String name = f.getName();
            return name.substring(name.lastIndexOf(".")).equals(extension);
        }).toList();
    }

    /**
     * returns true if the file was created or already existed.
     * False if it was unable to create said file.
     * @param absolutePath
     * @return
     */
    public static boolean createIfNotExists(String absolutePath) {
        File file = new File(absolutePath);

        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    return true;
                }
            } else {
                return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    public static String removeExtension(String fileName){
        return fileName.substring(0,fileName.lastIndexOf("."));
    }
}
