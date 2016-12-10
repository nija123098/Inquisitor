package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/10/2016
 */
@Register(rank = Rank.USER, hidden = true)
public class Mark {
    @Register(defaul = true, guild = true, suspicious = 3, help = "Be careful or it could reveal your command")
    public static void mark(User user, Guild guild, Channel channel, String s, Rank rank){
        String mark = guild.getID() + ":" + channel.getID();
        User u = null;
        if (s.length() != 0){
            if (!Rank.isSufficient(Rank.BOT_ADMIN, rank)){
                MessageHelper.send(channel, user.discord().mention() + ", you do not have permission to use another user's account for mark commands");
                return;
            }
            u = User.getUser(s);
            if (u != null){
                mark += ":" + u.getID();
            }else{
                MessageHelper.send(channel, "No account \"" + s + "\" found");
            }
        }
        user.putData("mark", mark);
    }
    @Register(suspicious = 1, help = "Invokes a command using the mark parameters")
    public static void invoke(User user, String s, IMessage message){
        if (user.getData("mark") != null){
            String[] mark = user.getData("mark").split(":");
            if (mark.length == 3){
                user = User.getUserFromID(mark[2]);
            }
            Invoke.invoke(user, Guild.getGuild(mark[0]), Channel.getChannel(mark[1]), s, message);
        }else{
            noMark(user);
        }
    }
    @Register(suspicious = .5f, help = "Displays info on the current mark")
    public static void info(User user){
        if (user.getData("mark") == null){
            noMark(user);
        }else{
            String[] mark = user.getData("mark").split(":");
            MessageHelper.send(user, "Marked " + Channel.getChannel(mark[1]).discord().getName() + " on guild " + Guild.getGuild(mark[0]).discord().getName() + (mark.length == 3 && User.getUserFromID(mark[2]) != null ? " using " + StringHelper.getPossessive(User.getUserFromID(mark[2]).discord().getName()) + " account" : ""));
        }
    }
    @Register(suspicious = 1.5f)
    public static void clear(User user){
        if (user.getData("mark") != null){
            user.clearData("mark");
            MessageHelper.send(user, "Mark data cleared");
        }else{
            noMark(user);
        }
    }
    private static void noMark(User user){
        MessageHelper.send(user, "No active mark");
    }
}
