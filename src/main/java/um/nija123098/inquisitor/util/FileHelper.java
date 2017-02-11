package um.nija123098.inquisitor.util;

import java.io.File;

/**
 * Made by nija123098 on 11/5/2016
 */
public class FileHelper {
    private static final String container;
    static{container = new File(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();}
    public static String getJarContainer(){
        return container;
    }
    private static int temp = -1;
    public static synchronized File getTemporaryFile(String type){
        File parent = new File(getJarContainer() + "\\temp\\");
        if (!parent.exists()){
            parent.mkdir();
        }
        File file = new File(parent, "temp" + ++temp + "." + type);
        file.deleteOnExit();
        return file;
    }
}
