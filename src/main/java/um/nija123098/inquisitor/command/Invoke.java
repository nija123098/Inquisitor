package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/7/2016
 */
public class Invoke {
    public static void invoke(String user, String guild, String channel, String msg){
        if (guild == null){
            invoke(User.getUser(user), null, Channel.getChannel(channel), msg);
        }else{
            invoke(User.getUser(user), Guild.getGuild(guild), Channel.getChannel(channel), msg);
        }
    }
    public static void invoke(User user, Guild guild, Channel channel, String msg){
        msg = StringHelper.limitOneSpace(msg);
        Command method = Registry.getCommand(msg);
        if (method != null){
            msg = msg.substring(method.name().length());
            if (msg.length() > 0){
                msg = msg.substring(1);
            }
            method.invoke(user, guild, channel, msg);
        }else{
            MessageHelper.send(channel, "Not a recognized command" + (channel.isPrivate() ? ", make sure you are not using a prefix" : ""));
        }
    }
}
