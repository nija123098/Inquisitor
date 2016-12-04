package um.nija123098.inquisitor.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.User;

import java.util.HashMap;
import java.util.Map;

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
    private static final Map<String, String> MAP = new HashMap<String, String>();
    public static String getEmoticon(String key){
        return MAP.get(key);
    }
    static {
        MAP.put("floppy_disk", "\uD83D\uDCBE");
        MAP.put("lock", "\uD83D\uDD12");
        MAP.put("x", "\u274C");
        MAP.put("question", "\u2753");
        MAP.put("ok_hand", "\ud83d\udc4c");
        MAP.put("tada", "\ud83D\uDC4C");
        MAP.put("no_entry", "\u26D4");
        MAP.put("no_entry_sign", "\ud83D\udEAB");
        MAP.put("spy", "\uD83D\uDD75");
        MAP.put("thumbs_up", "\uD83D\uDC4D");
    }
}
