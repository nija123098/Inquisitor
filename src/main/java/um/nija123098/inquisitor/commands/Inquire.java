package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageList;
import um.nija123098.inquisitor.command.Command;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.command.Suspicion;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.ListHelper;
import um.nija123098.inquisitor.util.MessageHelper;

import java.util.*;

/**
 * Made by nija123098 on 11/8/2016
 */
public class Inquire {
    @Register(defaul = true, help = "Displays information on the user, use \"help inquire commands\" to see more")
    public static void inquire(Channel channel, User user, String s){
        if (s.length() != 0){
            return;
        }
        MessageHelper.send(channel, "User " + user.getID() + " are you ready to be interrogated?\n" +
                user.discord().mention() + ", if that is your name, you so far have been " + Suspicion.getLevel(Float.parseFloat(user.getData("suspicion", "0"))));
    }
    @Register(help = "Lists all inquire commands")
    public static void commands(User user, Command command){
        CommonMessageHelper.displayHelp("# Inquire commands", "", Registry.getCommands(com -> com.name().split(" ")[0].equals(command.name().split(" ")[0]), com -> !com.hidden()), user);
    }
    @Register(suspicious = .125f, guild = true, help = "Lists all roles on a server")
    public static void roles(Guild guild, User user){
        try {
            IGuild iGuild = guild.discord();
            Map<Integer, Pair<String, Integer>> map = new HashMap<Integer, Pair<String, java.lang.Integer>>();
            iGuild.getRoles().forEach(role -> {
                final int[] count = {0};
                iGuild.getUsers().stream().filter(u -> u.getPresence().equals(Presences.ONLINE)).filter(u -> u.getRolesForGuild(iGuild).contains(role)).forEach(u -> ++count[0]);
                map.put(role.getPosition(), new Pair<String, Integer>(role.getName(), count[0]));
            });
            Pair<String, Integer>[] pairs = new Pair[map.size() + 1];
            map.forEach((integer, pair) -> pairs[integer] = pair);
            List<Pair<String, Integer>> pairList = new ArrayList<Pair<String, Integer>>(pairs.length);
            Collections.addAll(pairList, pairs);
            pairList.remove(null);
            ListHelper.flip(pairList);
            List<String> names = new ArrayList<String>(pairList.size()), online = new ArrayList<String>(pairList.size());
            pairList.forEach(pair -> {
                names.add(pair.getKey());
                online.add(pair.getValue() + "");
            });
            CommonMessageHelper.displayLists("# Roles for guild \"" + iGuild.getName() + "\"", "", names, online, user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Register(suspicious = .5f, guild = true, help = "Lists the permissions of a role on a server, use everyone instead of @everyone")
    public static void role(Guild guild, User user, String s){
        if (s.length() == 0){
            roles(guild, user);
        }else{
            if (s.equals("everyone")){
                s = "@everyone";
            }
            List<IRole> roles = guild.discord().getRolesByName(s);
            if (roles.size() == 0){
                MessageHelper.send(user, "No roles called \"" + s + "\"");
            }else{
                for (IRole iRole : roles) {
                    List<String> perms = new ArrayList<String>();
                    new ArrayList<Permissions>(iRole.getPermissions()).forEach(permissions -> perms.add(permissions.name()));
                    final int[] count = {0};
                    IGuild iGuild = guild.discord();
                    iGuild.getUsers().stream().filter(u -> u.getPresence().equals(Presences.ONLINE)).filter(u -> u.getRolesForGuild(iGuild).contains(iRole)).forEach(u -> ++count[0]);
                    CommonMessageHelper.displayList("# " + count[0] + " " + iRole.getName() + "s are online\n# Permisions for role " + iRole.getName(), "", perms, user);
                }
            }
        }
    }
    @Register
    public static void lang(Channel channel){
        MessageList messages = channel.discord().getMessages();
        String content;
        for (IMessage message : messages) {
            content = message.getContent();
            if (content.startsWith("```")) {
                content = content.replace("```", "").split("\n")[0];
                MessageHelper.send(channel, "The language used is \"" + content + "\"");
                return;
            }
        }
        MessageHelper.send(channel, "No language detected");
    }
}
