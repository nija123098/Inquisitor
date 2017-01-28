package um.nija123098.inquisitor.command;

import javafx.util.Pair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
    public static synchronized void register(List<Class> clazzes){
        final List<Method> methods = new ArrayList<>();
        clazzes.forEach(clazz -> Collections.addAll(methods, clazz.getMethods()));
        methods.forEach(method -> {
            if (method.isAnnotationPresent(Register.class)){
                Command command = new Command(method, methods);
                COMMANDS.add(command);
                if (command.surface()){
                    SURFACE.add(command);
                }else{
                    DEEP.add(command);
                }
            }
        });
    }
    public static void startUp(){
        COMMANDS.stream().filter(Command::startup).forEach(command -> command.invoke(null, null, null, null, null, false));
    }
    public static void shutDown(){
        COMMANDS.stream().filter(Command::shutdown).forEach(command -> command.invoke(null, null, null, null, null, false));
    }
    public static synchronized Triple<Command, Boolean, String> getCommand(String msg){
        Triple<Boolean, Boolean, String> pair;
        for (Command command : DEEP) {
            pair = match(msg, command);
            if (pair.getLeft()){
                return new ImmutableTriple<>(command, pair.getMiddle(), pair.getRight());
            }
        }
        for (Command command : SURFACE) {
            pair = match(msg, command);
            if (pair.getLeft()){
                return new ImmutableTriple<>(command, pair.getMiddle(), pair.getRight());
            }
        }
        return new ImmutableTriple<>(null, false, null);
    }
    private static Triple<Boolean, Boolean, String> match(String msg, Command command){
        for (String code : command.reactionAliases()){
            if (msg.startsWith(code)){
                return new ImmutableTriple<>(true, false, reduce(code, msg));
            }
        }
        for (String code : command.aliases()){
            if (msg.startsWith(code)){
                return new ImmutableTriple<>(true, false, reduce(code, msg));
            }
        }
        if (msg.startsWith(command.name())){
            return new ImmutableTriple<>(true, false, reduce(command.name(), msg));
        }
        return new ImmutableTriple<>(false, false, null);
    }
    private static String reduce(String code, String content){
        content = content.substring(code.length());
        if (content.startsWith(" ")){
            content = content.substring(1);
        }
        return content;
    }
    private static Pair<Boolean, Boolean> match(String[] msg, Command command){
        List<String> strings = command.reactionAliases();
        for (String string : strings) {
            if (msg[0].equals(string)) {
                return new Pair<>(true, false);
            }
        }
        for (String alias : command.aliases()){
            ArrayList<String[]> ref = new ArrayList<>();// these lines are a work around
            Collections.addAll(ref, alias.split(" "));// because the Intellij compiler is complaining
            for (String commandStrings[] : ref){
                System.out.println(alias);
                if (commandStrings.length > msg.length){
                    break;
                }
                for (int i = 0; i < commandStrings.length; i++) {
                    if (!commandStrings[i].equals(msg[i])) {
                        if (commandStrings.length > msg.length){
                            return new Pair<>(true, true);
                        }else{
                            break;
                        }
                    }
                }
            }
        }
        String[] commandStrings = command.name().split(" ");
        if (commandStrings.length > msg.length){
            return new Pair<>(false, false);
        }
        for (int i = 0; i < commandStrings.length; i++) {
            if (!commandStrings[i].equals(msg[i])) {
                return new Pair<>(commandStrings.length > msg.length, true);
            }
        }
        return new Pair<>(true, true);
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
