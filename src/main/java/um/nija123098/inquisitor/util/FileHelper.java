package um.nija123098.inquisitor.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class FileHelper {
    private static final String container;
    static{container = new File(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();}
    private static String getJarContainer(){
        return container;
    }
    public static boolean ensureFileExistence(String path) {
        path = getJarContainer() + "\\" + path;
        boolean existed;
        if (!(existed = Files.exists(Paths.get(path)))) {
            try{Files.createDirectory(Paths.get(path));
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return existed;
    }
    public static List<String> getStrings(String path){
        return getStringsNoAdjust(getJarContainer() + "\\");
    }
    public static List<String> getStringsNoAdjust(String path){
        try{
            List<String> strings = Files.readAllLines(Paths.get(path));
            if (strings == null){
                strings = new ArrayList<String>(0);
            }
            return strings;
        }catch(Exception ignored){
            return null;
        }
    }
    public static void writeStrings(String path, List<String> strings){
        try{Files.write(Paths.get(getJarContainer() + "\\" + path), strings);
        }catch(Exception ignored){ignored.printStackTrace();}
    }
    public static void delete(String path){
        try{Files.deleteIfExists(Paths.get(getJarContainer() + "\\" + path));
        }catch(Exception ignored){}
    }
    public static List<File> getFiles(String path){
        path = getJarContainer() + "\\" + path;
        List<File> files = new ArrayList<File>();
        try{Collections.addAll(files, new File(path).listFiles());
        }catch(Exception ignored){}
        return files;
    }
    public static List<String> getPaths(String path){
        List<String> strings = new ArrayList<String>();
        ensureFileExistence(path);
        for (File file : new File(getJarContainer() + "\\" + path).listFiles()) {
            strings.add(path + "\\" + file.getName());
        }
        return strings;
    }
    public static void cleanDir(String path){
        File file = new File(path);
        if (file.exists()){
            if (file.isDirectory()){
                cleanDir(file.getPath());
                file.delete();
            }else{
                file.delete();
            }
        }
    }
    public static String getUniqueName(String path){
        int i = 0;
        List<File> files = getFiles(path);
        while (true){
            boolean found = false;
            for (File file : files) {
                if (file.getName().equals("uniquefile" + i)){
                    ++i;
                    found = true;
                    break;
                }
            }
            if (!found){
                return "uniquefile" + i;
            }
        }
    }
}
