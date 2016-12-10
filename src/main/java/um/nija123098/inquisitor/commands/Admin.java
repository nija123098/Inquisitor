package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/20/2016
 */
@Register(rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL)
public class Admin {
    @Register(defaul = true, override = true, help = "Lists bot admins")
    public static void admin(User user, Entity entity){
        List<String> strings = new ArrayList<String>();
        String[] strs = entity.getData("admins", "").split(":");
        for (int i = 0; i < strs.length; i++) {
            User admin = User.getUserFromID(strs[i]);
            strings.add(admin.discord().getName() + "#" + admin.discord().getDiscriminator());
        }
        CommonMessageHelper.displayList("# A list of Inquisitor admins", "", strings, user);
    }
    @Register(rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Makes a user a bot admin")
    public static void op(Channel channel, String s, Entity entity){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if ("true".equals(user.getData("admin"))) {
            MessageHelper.send(channel, user.discord().mention() + " is already a " + Inquisitor.ourUser().mention() + " admin!");
        }else{
            user.putData("admin", true + "");
            entity.putData("admins", entity.getData("admins", "") + user.getID() + ":");
            MessageHelper.send(channel, user.discord().mention() + " is now a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Removes a user as a bot admin")
    public static void unop(Channel channel, String s, Entity entity){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if (user.getData("admin") == null){
            MessageHelper.send(channel, user.discord().mention() + " not a " + Inquisitor.ourUser().mention() + " admin!");
        }else{
            user.clearData("admin");
            entity.putData("admins", entity.getData("admins").replace(user.getID() + ":", ""));
            MessageHelper.send(channel, user.discord().mention() + " is no longer a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(help = "Bans a user from using the bot")
    public static void ban(Channel channel, String s) {
        User user = User.getUser(s);
        if (user == null) {
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        } else if (user.getData("banned").equals("true")){
            MessageHelper.send(channel, user.discord().mention() + " has already been banned from using " + Inquisitor.ourUser().mention() + "!");
        }else {
            user.putData("banned", true + "");
            MessageHelper.send(channel, user.discord().mention() + " is now banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register(help = "Unbans a user from using the bot")
    public static void unban(Channel channel, String s){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else if (user.getData("banned") == null){
            MessageHelper.send(channel, user.discord().mention() + " was not banned from using " + Inquisitor.ourUser().mention() + "!");
        }else{
            user.putData("admin", false + "");
            user.clearData("banned");
            MessageHelper.send(channel, user.discord().mention() + " is no longer banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register
    public static void lockdown(User user, IMessage message){
        Inquisitor.lockdown();
        Inquisitor.discordClient().getShards().forEach(iShard -> iShard.changePresence(true));
        MessageHelper.react("lock", message);
        Log.warn(user.discord().getName() + " put Inquisitor in lockdown");
    }
    @Register(help = "Saves all bot configuration files")
    public static void save(IMessage message){
        Inquisitor.save();
        MessageHelper.react("floppy_disk", message);
    }
    @Register(help = "Shuts down the bot without restart")
    public static void close(User user, IMessage message){
        Inquisitor.lockdown();
        MessageHelper.react("lock_and_key", message);
        Log.warn(user.discord().getName() + " is closing Inquisitor");
        Inquisitor.close();
    }
}
