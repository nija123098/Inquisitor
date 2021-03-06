package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/10/2016
 */
@Register(rank = Rank.USER, hidden = true)
public class Mark {
    @Register(defaul = true, guild = true, suspicious = 3, help = "Be careful or it could reveal your command")
    public static Boolean mark(User user, Guild guild, Channel channel, String s, Rank rank, Entity entity, MessageAid aid){
        String mark = guild.getID() + ":" + channel.getID();
        User u;
        if (s.length() != 0){
            if (!Rank.isSufficient(Rank.MAKER, rank)){
                aid.withToggleContent(false, user.discord().getDisplayName(guild.discord()), ", you do not have permission to use another user's account for mark commands");
                return false;
            }
            u = User.getUser(s);
            if (u != null){
                mark += ":" + u.getID();
            }else{
                aid.withToggleContent(false, "No account ", StringHelper.addQuotes(s), "found");
            }
        }
        entity.putData(user, mark);
        return true;
    }
    @Register(suspicious = 1, help = "Invokes a command using the mark parameters")
    public static Boolean invoke(User user, String s, IMessage message, Entity entity){
        if (entity.getData(user) != null){
            String[] mark = entity.getData(user).split(":");
            if (mark.length == 3){
                user = User.getUserFromID(mark[2]);
            }
            return Invoke.invoke(user, Guild.getGuild(mark[0]), Channel.getChannel(mark[1]), s, message);
        }else{
            noMark(user);
        }
        return false;
    }
    @Register(suspicious = .5f, help = "Displays info on the current mark")
    public static Boolean info(User user, Entity entity){
        if (entity.getData(user) == null){
            noMark(user);
            return false;
        }
        String[] mark = entity.getData(user).split(":");
        MessageHelper.send(user, "Marked " + Channel.getChannel(mark[1]).discord().getName() + " on guild " + Guild.getGuild(mark[0]).discord().getName() + (mark.length == 3 && User.getUserFromID(mark[2]) != null ? " using " + StringHelper.getPossessive(User.getUserFromID(mark[2]).discord().getName()) + " account" : ""));
        return true;
    }
    @Register(suspicious = 1.5f)
    public static void clear(User user, Entity entity){
        if (entity.getData(user) != null){
            entity.clearData(user);
            MessageHelper.send(user, "Mark data cleared");
        }else{
            noMark(user);
        }
    }
    private static void noMark(User user){
        MessageHelper.send(user, "No active mark");
    }
}
