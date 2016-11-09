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
        COMMANDS = new ArrayList<Command>();
        SURFACE = new ArrayList<Command>();
        DEEP = new ArrayList<Command>();
    }
    public static void register(Class clazz){
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
        COMMANDS.stream().filter(Command::startup).forEach(command -> command.invoke(null, null, null, null));
    }
    public static Command getCommand(String msg){
        msg = msg.toLowerCase();
        for (Command command : DEEP) {
            if (match(msg, command)){
                return command;
            }
        }
        for (Command command : SURFACE) {
            if (match(msg, command)){
                return command;
            }
        }
        return null;
    }
    private static boolean match(String msg, Command command){
        String[] commandStrings = command.name().split(" ");
        String[] msgStrings = msg.split(" ");
        if (commandStrings.length > msgStrings.length){
            return false;
        }
        for (int i = 0; i < commandStrings.length; i++) {
            if (!commandStrings[i].equals(msgStrings[i])){
                return false;
            }
        }
        return true;
    }
    public static List<Command> getCommands(Predicate<Command> predicate){
        return COMMANDS.stream().filter(predicate).collect(Collectors.toList());
    }
}
