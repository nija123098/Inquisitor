package um.nija123098.inquisitor.commands;

import org.eclipse.jetty.util.ConcurrentHashSet;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.util.MessageHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 4/13/2017.
 */
public class GameWatch {
    private static final Map<String, Set<String>> MAP = new ConcurrentHashMap<>();
    @Register(startup = true)
    public static void startup(Entity entity){
        entity.getSaved().forEach(s -> {
            Set<String> set = new ConcurrentHashSet<>();
            Collections.addAll(set, entity.getData(s).split(", "));
            MAP.put(s, set);
        });
        entity.clearData();
        Inquisitor.registerListener(GameWatch.class);
    }
    @Register(shutdown = true)
    public static void shutdown(Entity entity){
        MAP.forEach((s, strings) -> {
            StringBuilder builder = new StringBuilder();
            strings.forEach(st -> builder.append(st).append(", "));
            entity.putData(s, builder.toString());
        });
    }
    @Register(defaul = true)
    public static boolean gameWatch(String s, Guild guild, Channel channel){
        User target = User.getUser(s, guild);
        if (target == null){
            MessageHelper.send(channel, "That is not a known user");
            return false;
        }
        if (!MAP.containsKey(target.getID())){
            MessageHelper.send(channel, "That user has no played games");
            return false;
        }
        StringBuilder builder = new StringBuilder("That user has played: ");
        MAP.get(target.getID()).forEach(val -> builder.append(val).append(", "));
        MessageHelper.send(channel, builder.substring(0, builder.length() - 2));
        return true;
    }
    @EventSubscriber
    public static void handle(PresenceUpdateEvent event){
        event.getNewPresence().getPlayingText().ifPresent(s -> {
            Set<String> set = MAP.computeIfAbsent(event.getUser().getID(), st -> new ConcurrentHashSet<>());
            if (!set.contains(s)){
                set.add(s);
            }
        });
    }
}
