package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.ContextHelper;
import um.nija123098.inquisitor.util.Regard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Made by nija123098 on 11/6/2016
 */
public class Registry {
    private static final List<Class> commands;
    static {
        commands = new ArrayList<Class>();
    }
    public static void registerCommand(Class c){
        commands.add(c);
    }
    public static Method getCommand(String command){
        String[] parts = command.split(" ");
        for (Class clazz : commands) {
            if (clazz.isAnnotationPresent(Natural.class)){
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(Command.class) && method.getName().toLowerCase().equals(parts[0].toLowerCase())){
                        return method;
                    }
                }
            }
            if (clazz.getSimpleName().toLowerCase().equals(parts[0].toLowerCase())){
                if (parts.length == 1){
                    for (Method method : clazz.getMethods()) {
                        if (method.isAnnotationPresent(Natural.class)){
                            return method;
                        }
                    }
                }
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(Command.class) && method.getName().toLowerCase().equals(parts[1].toLowerCase())){
                        return method;
                    }
                }
            }
        }
        return null;
    }
    private static final List<Method> listeners;
    static {
        listeners = new ArrayList<Method>();
    }
    public static void registerListener(Class c){
        for (Method method : c.getMethods()) {
            if (method.isAnnotationPresent(Listener.class)){
                listeners.add(method);
            }
        }
    }
    public static void listen(boolean admin, User user, Guild guild, Channel channel, String s){
        listeners.forEach(method -> ContextHelper.execute(method, admin, user, guild, channel, s));
    }
    private static final List<Method> starters;
    static {
        starters = new ArrayList<Method>();
    }
    public static void registerStarter(Class c){
        for (Method method : c.getMethods()) {
            if (method.isAnnotationPresent(Starter.class) && method.getParameterTypes().length == 0){
                starters.add(method);
            }
        }
    }
    public static void start(){
        starters.forEach(method -> Regard.less(() -> method.invoke(null)));
    }
    private static final List<Class> timerTaskTypes;
    static {
        timerTaskTypes = new ArrayList<Class>();
    }
    public static void registerTimerTask(Class c){
        for (Class a : c.getInterfaces()) {
            if (a.equals(TimerTask.class)){
                for (Constructor constructor : a.getConstructors()) {
                    if (!constructor.isAccessible()){
                        continue;
                    }
                    if (constructor.getParameterTypes().length == 0){
                        timerTaskTypes.add(c);
                        return;
                    }else if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(List.class)){
                        timerTaskTypes.add(c);
                        return;
                    }
                }
            }
        }
    }
    public static Class getTimerTask(String className){
        for (Class c : timerTaskTypes) {
            if (c.getName().equals(className)){
                return c;
            }
        }
        return null;
    }
}
