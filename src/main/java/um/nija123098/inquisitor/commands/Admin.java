package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 11/20/2016
 */
@Register(natural = true, rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL)
public class Admin {
    @Register(natural = true, rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Makes a user a bot admin")
    public static void makeAdmin(Channel channel, String s){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else{
            MessageHelper.send(channel, user.discord().mention() + " is now a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(natural = true, rank = Rank.MAKER, suspicion = Suspicion.HERETICAL, override = true, help = "Removes a user as a bot admin")
    public static void removeAdmin(Channel channel, String s){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else{
            MessageHelper.send(channel, user.discord().mention() + " is no longer a " + Inquisitor.ourUser().mention() + " admin!");
        }
    }
    @Register(natural = true, help = "Bans a user from using the bot")
    public static void ban(Channel channel, String s){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else{
            MessageHelper.send(channel, user.discord().mention() + " is now banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register(natural = true, help = "Unbans a user from using the bot")
    public static void unban(Channel channel, String s){
        User user = User.getUser(s);
        if (user == null){
            MessageHelper.send(channel, "\"" + s + "\" is not a known user");
        }else{
            MessageHelper.send(channel, user.discord().mention() + " is no longer banned from using " + Inquisitor.ourUser().mention() + "!");
        }
    }
    @Register
    public static void lockdown(User user){
        Inquisitor.lockdown();
        Inquisitor.discordClient().changePresence(true);
        Log.warn(user.discord().getName() + " put Inquisitor in lockdown");
    }
    @Register(help = "Saves all bot configuration files")
    public static void save(Channel channel){
        Inquisitor.save();
        MessageHelper.send(channel, "*Files saved*");
    }
    @Register(help = "Shuts down the bot without restart")
    public static void close(User user){
        Inquisitor.lockdown();
        Log.warn(user.discord().getName() + " is closing Inquisitor");
        Inquisitor.close();
    }
}
