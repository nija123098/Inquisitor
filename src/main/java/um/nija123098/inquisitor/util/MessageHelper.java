package um.nija123098.inquisitor.util;

import sx.blah.discord.util.MessageBuilder;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.User;

/**
 * Made by nija123098 on 11/6/2016
 */
public class MessageHelper {
    public static void sendOverride(Channel channel, String msg){
        RequestHandler.request(() -> channel.discordChannel().sendMessage(msg));
    }
    public static void send(Channel channel, String msg){
        if ("true".equals(channel.getData("chat_approved"))){
            sendOverride(channel, msg);
        }
    }
    public static void send(User user, String msg){
        RequestHandler.request(() -> new MessageBuilder(Inquisitor.inquisitor().getClient()).withChannel(user.discordUser().getOrCreatePMChannel()).withContent(msg).send());
    }
}
