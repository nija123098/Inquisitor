package um.nija123098.inquisitor.command;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import sx.blah.discord.handle.impl.obj.Reaction;
import um.nija123098.inquisitor.util.EmoticonHelper;

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
        COMMANDS.stream().filter(Command::startup).forEach(command -> command.invoke(null, null, null, null, null, null, false));
    }
    public static void shutDown(){
        COMMANDS.stream().filter(Command::shutdown).forEach(command -> command.invoke(null, null, null, null, null, null, false));
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
    public static synchronized Command getReactionCommand(String code){
        if (!EmoticonHelper.isReaction(code)){
            return null;
        }
        for (Command command : COMMANDS){
            if (command.reactionAliases().contains(code)){
                return command;
            }
        }
        return null;
    }
    private static Triple<Boolean, Boolean, String> match(String msg, Command command){
        String low = msg.toLowerCase();
        for (String code : command.reactionAliases()){
            if (match(code, msg)){
                return new ImmutableTriple<>(true, false, reduce(code, msg));
            }
        }
        for (String code : command.aliases()){
            if (match(low, msg)){
                return new ImmutableTriple<>(true, false, reduce(code, msg));
            }
        }
        if (match(command.name(), low)){
            return new ImmutableTriple<>(true, false, reduce(command.name(), msg));
        }
        return new ImmutableTriple<>(false, false, null);
    }
    private static boolean match(String code, String msg){
        return msg.startsWith(code) && (msg.length() == code.length() || msg.charAt(code.length()) == ' ');
    }
    private static String reduce(String code, String content){
        content = content.substring(code.length());
        if (content.startsWith(" ")){
            content = content.substring(1);
        }
        return content;
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
