package um.nija123098.inquisitor.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

/**
 * Made by nija123098 on 11/6/2016
 */
public class MessageHelper {
    public static void sendOverride(Channel channel, String msg, long deleteMillis){
        innerSend(channel.discord(), msg, deleteMillis);
    }
    public static void sendOverride(Channel channel, String msg){
        sendOverride(channel, msg, 0);
    }
    public static void send(Channel channel, String msg){
        if ("true".equals(channel.getData("chat_approved"))){
            sendOverride(channel, msg);
        }
    }
    public static void send(Channel channel, String msg, long deleteMillis){
        if ("true".equals(channel.getData("chat_approved"))){
            sendOverride(channel, msg, deleteMillis);
        }
    }
    public static void send(User user, String msg, long deleteMillis){
        RequestHandler.request(() -> innerSend(user.discord().getOrCreatePMChannel(), msg, deleteMillis));
    }
    public static void send(User user, String msg){
        send(user, msg, 0);
    }
    public static void send(Guild guild, String msg){
        String liaison = guild.getData("liaison");
        if (liaison == null){
            return;
        }
        send(User.getUserFromID(liaison), "Regarding guild " + guild.discord().getName() + "\n" + msg);
    }
    private static void innerSend(IChannel channel, String msg, long deleteMillis){
        RequestHandler.request(() -> {
            IMessage message = new MessageBuilder(Inquisitor.discordClient()).withChannel(channel).withContent('\u200B' + msg).send();
            if (deleteMillis > 0){
                RequestHandler.request(deleteMillis, message::delete);
            }
        });
    }
    public static void checkYourDMs(Channel channel, User user){
        if (!channel.isPrivate()){
            send(channel, user.discord().mention() + " check your DMs!", 20000);
        }
    }
    public static boolean react(String s, IMessage message){
        String val = getEmoticon(s);
        if (val != null){
            RequestHandler.request(() -> message.addReaction(val));
            return true;
        }else{
            Log.warn("No emoticon " + StringHelper.addQuotes(s));
            return false;
        }
    }
    private static final Entity EMOTICON_ENTITY;
    static {
        EMOTICON_ENTITY = Inquisitor.getEntity("emoticons");
    }
    public static String getEmoticon(String key){
        return EMOTICON_ENTITY.getData(key, "unknown");
    }
}
