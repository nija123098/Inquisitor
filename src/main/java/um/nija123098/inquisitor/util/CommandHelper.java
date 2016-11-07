package um.nija123098.inquisitor.util;

import um.nija123098.inquisitor.command.Natural;
import um.nija123098.inquisitor.command.Supported;

import java.lang.reflect.Method;

/**
 * Made by nija123098 on 11/6/2016
 */
public class CommandHelper {
    public static String help(Method method){
        return method.isAnnotationPresent(Supported.class) ? method.getAnnotation(Supported.class).help() : "Not a supported command";
    }
    public static boolean isSurfaceCommand(Method method){
        return method.getDeclaringClass().isAnnotationPresent(Natural.class) || method.isAnnotationPresent(Natural.class);
    }
    public static String command(Method method){
        if (method.isAnnotationPresent(Natural.class) || method.getDeclaringClass().isAnnotationPresent(Natural.class)){
            return method.getName();
        }else{
            return method.getDeclaringClass().getSimpleName().toLowerCase() + " " + method.getName();
        }
    }
}
