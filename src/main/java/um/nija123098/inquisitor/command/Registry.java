package um.nija123098.inquisitor.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 11/7/2016
 */
public class Registry {
    private static final List<Command> COMMANDS;
    private static final List<Command> SURFACE;
    private static final List<Command> DEEP;
    static {
        COMMANDS = new ArrayList<>();
        SURFACE = new ArrayList<>();
        DEEP = new ArrayList<>();
    }
    public static synchronized void register(Class clazz){
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Register.class)){
                Command command = new Command(method);
                COMMANDS.add(command);
                if (command.surface()){
                    SURFACE.add(command);
                }else{
                    DEEP.add(command);
                }
            }
        }
    }
    public static void startUp(){
        COMMANDS.stream().filter(Command::startup).forEach(command -> command.invoke(null, null, null, null, null));
    }
    public static void shutDown(){
        COMMANDS.stream().filter(Command::shutdown).forEach(command -> command.invoke(null, null, null, null, null));
    }
    public static synchronized Command getCommand(String msg){
        String[] strings = msg.toLowerCase().split(" ");
        for (Command command : DEEP) {
            if (match(strings, command)){
                return command;
            }
        }
        for (Command command : SURFACE) {
            if (match(strings, command)){
                return command;
            }
        }
        return null;
    }
    private static boolean match(String[] msg, Command command){
        String[] commandStrings = command.name().split(" ");
        if (commandStrings.length > msg.length){
            return false;
        }
        for (int i = 0; i < commandStrings.length; i++) {
            if (!commandStrings[i].equals(msg[i])){
                if (!command.args() && commandStrings.length != msg.length){
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    @SafeVarargs
    public static List<Command> getCommands(Predicate<Command>...predicates){
        List<Command> commands = new ArrayList<>(COMMANDS.size());
        commands.addAll(COMMANDS);
        for (Predicate<Command> predicate : predicates) {
            commands = commands.stream().filter(predicate).collect(Collectors.toList());
        }
        return commands;
    }
}
