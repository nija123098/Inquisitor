package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Status;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/20/2016
 */
@Register(rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL)
public class Admin {
    @Register(defaul = true, override = true, help = "Lists bot admins")
    public static void admin(User user){
        Entity entity = Inquisitor.getEntity("permissions");
        List<String> strings = new ArrayList<>();
        String[] strs = entity.getData("admin", "").split(":");
        for (String str : strs) {
            if (str.length() == 0){
                continue;
            }
            User admin = User.getUserFromID(str);
            if (admin != null){
                strings.add(admin.discord().getName() + "#" + admin.discord().getDiscriminator());
            }else{
                Log.warn("No admin: " + str);
            }
        }
        CommonMessageHelper.displayList("# A list of Inquisitor admins", "", strings, user);
    }
    @Register(rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Makes a user a bot admin")
    public static void authorize(Channel channel, String s){
        Entity entity = Inquisitor.getEntity("permissions");
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if (!(":" + entity.getData("admin")).contains(":" + user.getID() + ":")) {
            MessageHelper.send(channel, user.discord().mention() + " is already a " + Inquisitor.ourUser().mention() + " admin!");
        }else{
            entity.putData("admin", entity.getData("admin") + user + ":");
            MessageHelper.send(channel, user.discord().mention() + " is now a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Removes a user as a bot admin")
    public static void unauthorize(Channel channel, String s){
        Entity entity = Inquisitor.getEntity("permissions");
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if ((":" + entity.getData("admin")).contains(":" + user + ":")){
            MessageHelper.send(channel, user.discord().mention() + " not a " + Inquisitor.ourUser().mention() + " admin!");
        }else{
            entity.putData("admin", entity.getData("admins").replace(user.getID() + ":", ""));
            MessageHelper.send(channel, user.discord().mention() + " is no longer a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(help = "Bans a user from using the bot")
    public static void ban(Channel channel, String s) {
        Entity entity = Inquisitor.getEntity("permissions");
        User user = User.getUser(s);
        if (user == null) {
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        } else if ((":" + entity.getData("banned", "")).contains(":" + user.getID() + ":")){
            MessageHelper.send(channel, user.discord().mention() + " has already been banned from using " + Inquisitor.ourUser().mention() + "!");
        } else {
            entity.putData("banned", entity.getData("banned") + user.getID() + ":");
            MessageHelper.send(channel, user.discord().mention() + " is now banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register(help = "Unbans a user from using the bot")
    public static void unban(Channel channel, String s){
        Entity entity = Inquisitor.getEntity("permissions");
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if (!(":" + entity.getData("banned")).contains(":" + user.getID() + ":")){
            MessageHelper.send(channel, user.discord().mention() + " was not banned from using " + Inquisitor.ourUser().mention() + "!");
        }else{
            entity.putData("banned", entity.getData("banned").replace("", user.getID() + ":"));
            MessageHelper.send(channel, user.discord().mention() + " is no longer banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register
    public static void lockdown(User user, IMessage message){
        Inquisitor.lockdown();
        Inquisitor.discordClient().getShards().forEach(iShard -> iShard.changePresence(true));
        Inquisitor.discordClient().getShards().forEach(iShard -> iShard.changeStatus(Status.game("with locks")));
        MessageHelper.react("lock", message);
        Log.warn(user.discord().getName() + " put Inquisitor in lockdown");
    }
    @Register(emoticonAliases = "floppy_disk", help = "Saves all bot configuration files")
    public static void save(IMessage message){
        Inquisitor.save();
        MessageHelper.react("floppy_disk", message);
    }
    @Register(help = "Shuts down the bot without restart")
    public static void close(User user, IMessage message, String s){
        Inquisitor.lockdown();
        switch (s){
            case "":
            case "update":
                Inquisitor.setExitCode(1);
                break;
            case "down":
                Inquisitor.setExitCode(0);
                break;
            default:
                MessageHelper.react("question", message);
                return;
        }
        MessageHelper.react("closed_lock_with_key", message);
        Log.warn(user.discord().getName() + " is closing Inquisitor");
        Inquisitor.close();
    }
    @Register(hidden = true)
    public static void exit(String s){
        System.exit(Integer.parseInt(s));
    }
    @Register(help = "Adds a reaction character, :chars: code")
    public static void addReaction(String s, IMessage message){
        String[] strings = s.split(" ");
        EmoticonHelper.addReaction(strings[1], strings[0]);
        MessageHelper.react(strings[1], message);
    }
}
