package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.command.Suspicion;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 11/20/2016
 */
@Register(natural = true, rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL)
public class Admin {
    @Register
    public static void lockdown(User user){
        Inquisitor.lockdown();
        Inquisitor.discordClient().changePresence(true);
        Log.warn(user.discord().getName() + " put Inquisitor in lockdown");
    }
    @Register(rank = Rank.BOT_ADMIN, help = "Saves all bot configuration files")
    public static void save(Channel channel){
        Inquisitor.save();
        MessageHelper.send(channel, "*Files saved*");
    }
    @Register(rank = Rank.BOT_ADMIN, help = "Shuts down the bot without restart")
    public static void close(User user){
        Inquisitor.lockdown();
        Log.warn(user.discord().getName() + " is closing Inquisitor");
        Inquisitor.close();
    }
}
