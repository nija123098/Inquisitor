package um.nija123098.inquisitor.command;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import sx.blah.discord.handle.impl.obj.Reaction;
import um.nija123098.inquisitor.util.ClassFinder;
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
    private static final List<List<Triple<String, String[], Command>>> COMMAND_TRIPLE;
    private static final List<Command> COMMANDS;
    static {
        COMMAND_TRIPLE = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            COMMAND_TRIPLE.add(new ArrayList<>());
        }
        COMMANDS = new ArrayList<>();
    }
    public static synchronized void register(List<Class> clazzes){
        final List<Method> methods = new ArrayList<>();
        clazzes.forEach(clazz -> Collections.addAll(methods, clazz.getMethods()));
        methods.forEach(method -> {
            if (method.isAnnotationPresent(Register.class)){
                Command command = new Command(method, methods);
                command.names().forEach(s -> {
                    String[] strings = s.split(" ");
                    if (COMMAND_TRIPLE.get(strings.length) == null){
                        COMMAND_TRIPLE.add(strings.length, new ArrayList<>());
                    }
                    COMMAND_TRIPLE.get(strings.length).add(new ImmutableTriple<>(s, s.split(" "), command));
                });
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
        String[] lows = msg.toLowerCase().toLowerCase().split(" ");
        for (int k = lows.length; k > 0; k--) {
            for (Triple<String, String[], Command> trip : COMMAND_TRIPLE.get(k)){
                boolean matches = true;
                for (int i = 0; i < trip.getMiddle().length; i++) {
                    if (!trip.getMiddle()[i].equals(lows[i])){
                        matches = false;
                        break;
                    }
                }
                if (matches){
                    String s = msg.substring(trip.getLeft().length());
                    if (s.startsWith(" ")){
                        s = s.substring(1);
                    }
                    return new ImmutableTriple<>(trip.getRight(), !trip.getRight().reactionAliases().contains(trip.getLeft()), s);
                }
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
                return new ImmutableTriple<>(true, true, reduce(code, msg));
            }
        }
        for (String code : command.aliases()){
            if (match(low, msg)){
                return new ImmutableTriple<>(true, true, reduce(code, msg));
            }
        }
        if (match(command.name(), low)){
            return new ImmutableTriple<>(true, true, reduce(command.name(), msg));
        }
        return new ImmutableTriple<>(false, false, null);
    }
    private static boolean match(String code, String msg){
        int mss = msg.split(" ").length;
        int clds = msg.split(" ").length;
        return msg.startsWith(code) && ((msg.length() == code.length() && mss == clds) || (msg.charAt(code.length()) == ' ') && mss > clds);
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
