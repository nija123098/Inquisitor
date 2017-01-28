package um.nija123098.inquisitor.command;

import org.apache.commons.lang3.tuple.Triple;
import sx.blah.discord.handle.obj.IMessage;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/7/2016
 */
public class Invoke {
    public static boolean invoke(String user, String guild, String channel, String msg, IMessage iMessage){
        if (guild == null){
            return invoke(User.getUserFromID(user), null, Channel.getChannel(channel), msg, iMessage);
        }else{
            return invoke(User.getUserFromID(user), Guild.getGuild(guild), Channel.getChannel(channel), msg, iMessage);
        }
    }
    public static boolean invoke(User user, Guild guild, Channel channel, String msg, IMessage iMessage){
        Triple<Command, Boolean, String> pair = Registry.getCommand(StringHelper.limitOneSpace(msg));
        if (pair.getLeft() != null){
            return pair.getLeft().invoke(user, guild, channel, pair.getRight(), iMessage, pair.getMiddle());
        }else{
            MessageHelper.react("question", iMessage);
            if (guild == null){
                MessageHelper.send(channel, "There is no need to have a prefix in a DM.");
            }
        }
        return false;
    }
}
