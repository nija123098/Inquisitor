package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageList;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

import java.util.*;

/**
 * Made by nija123098 on 11/8/2016
 */
public class Inquire {
    @Register(defaul = true, help = "Displays information on the user")
    public static Boolean inquire(Guild guild, Channel channel, User user, String s, IMessage message){
        if (s.length() == 0){
            MessageHelper.send(channel, "User " + user.getID() + " are you ready to be interrogated?\n" +
                    user.discord().mention() + ", if that is your name, you so far have been " + Suspicion.getLevel(user));
        }else{
            if ((user = User.getUser(s)) == null){
                MessageHelper.send(channel, "There is no known user by the name of " + StringHelper.addQuotes(s));
                return false;
            }
            MessageHelper.react("eye", message);
            Suspicion.addLevel(user, .1f, null, false);
            MessageHelper.send(channel, "User " + user.getID() + " goes by the name of " + (guild == null ? user.discord().getName() : user.discord().getDisplayName(guild.discord())) + "\n" +
                    (guild != null && !user.discord().getDisplayName(guild.discord()).equals(user.discord().getName()) ? user.discord().getDisplayName(guild.discord()) + " is actually " + user.discord().getName() + "\n" : "") +
                    user.discord().getName() + " has thus far been " + Suspicion.getLevel(user) + " and is a " + Rank.getRankName(user, guild));
        }
        return true;
    }
    @Register(suspicious = .125f, guild = true, help = "Lists all roles on a server")
    public static void roles(Guild guild, User user){
        IGuild iGuild = guild.discord();
        Map<Integer, Pair<String, Long>> map = new HashMap<>();
        int size = iGuild.getRoles().size();
        List<String> names = new ArrayList<>(size), online = new ArrayList<>(size);
        iGuild.getRoles().forEach(iRole -> map.put(iRole.getPosition(), new Pair<>(iRole.getName(), iGuild.getUsers().stream().filter(iUser -> iUser.getRolesForGuild(iGuild).contains(iRole) && iUser.getPresence().getStatus() == StatusType.ONLINE).count())));
        map.forEach((integer, stringLongPair) -> {
            names.add(integer, stringLongPair.getKey());
            online.add(integer, stringLongPair.getValue() + "");
        });
        Collections.reverse(names);
        Collections.reverse(online);
        CommonMessageHelper.displayLists("# Roles for guild \"" + iGuild.getName() + "\"", "", names, online, user);
    }
    @Register(suspicious = .5f, guild = true, help = "Lists the permissions of a role on a server, use everyone instead of @everyone")
    public static Boolean role(Guild guild, User user, String s, MessageAid aid){
        if (s.length() == 0){
            roles(guild, user);
        }else{
            if (s.equals("everyone")){
                s = "@everyone";
            }
            List<IRole> roles = guild.discord().getRolesByName(s);
            if (roles.size() == 0){
                aid.withToggleContent(false, "No roles called ", StringHelper.addQuotes(s));
                return false;
            }
            for (IRole iRole : roles) {
                List<String> perms = new ArrayList<>();
                new ArrayList<>(iRole.getPermissions()).forEach(permissions -> perms.add(permissions.name()));
                final int[] count = {0};
                IGuild iGuild = guild.discord();
                iGuild.getUsers().stream().filter(u -> u.getPresence().getStatus() == StatusType.ONLINE).filter(u -> u.getRolesForGuild(iGuild).contains(iRole)).forEach(u -> ++count[0]);
                CommonMessageHelper.displayList("# " + count[0] + " " + iRole.getName() + "s are online\n# Permisions for role " + iRole.getName(), "", perms, user);
            }
        }
        return true;
    }
    @Register(help = "Displays the language of a previous message")
    public static Boolean lang(Channel channel, MessageAid aid){
        MessageList messages = channel.discord().getMessages();
        String content;
        for (IMessage message : messages) {
            content = message.getContent();
            if (content.startsWith("```")) {
                content = content.replace("```", "").split("\n")[0];
                aid.withToggleContent(false, "The language used is ", StringHelper.addQuotes(content));
                return true;
            }
        }
        aid.withContent("No language detected");
        return false;
    }
    @Register(help = "Reads out the activity of the given user on the server for the last week")
    public static Boolean activity(Guild guild, String s, MessageAid aid){
        User inquired = User.getUser(s);
        if (inquired == null){
            aid.withToggleContent(true, StringHelper.addQuotes(s), " is not a known user.");
            return false;
        }
        long current = System.currentTimeMillis();
        final int[] count = new int[1];
        StringHelper.getContentList(inquired.getData("tracking" + "message" + guild.getID(), "").split(":")).stream().filter(st -> current - Long.parseLong(st) < 604800000).forEach(st -> ++count[0]);
        aid.withToggleContent(true, inquired.discord().getName(), " has sent ", count[0] + "", " messages in the last week");
        return true;
    }
    @Register(startup = true, rank = Rank.NONE)
    public static void startup(){
        Inquisitor.registerListener(new ActivityMonitor());
    }
    public static class ActivityMonitor {
        @EventSubscriber
        public void handle(MessageReceivedEvent event){
            String guildId = "null";
            if (!event.getMessage().getChannel().isPrivate()){
                guildId = event.getMessage().getGuild().getID();
            }
            adjustActivity("message", guildId, User.getUserFromID(event.getMessage().getAuthor().getID()));
        }
        public static void adjustActivity(String type, String guild, User user){
            String id = "tracking" + type + guild;
            user.putData(id, user.getData(id, "") + System.currentTimeMillis() + ":");
        }
    }
}
