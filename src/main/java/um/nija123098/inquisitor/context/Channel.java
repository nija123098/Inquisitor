package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.util.FileHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Channel extends Context {
    private static final List<Channel> CHANNELS;
    static {
        CHANNELS = new ArrayList<Channel>();
        FileHelper.ensureFileExistence("channels");
    }
    public static Channel getChannel(String id){
        for (Channel channel : CHANNELS) {
            if (channel.getID().equals(id)){
                return channel;
            }
        }
        Channel channel = new Channel(id);
        CHANNELS.add(channel);
        return channel;
    }
    public Channel(String id) {
        super("channel", id);
        if (this.discord().getName().contains("test") || this.discord().getName().contains("spam") || this.isPrivate()){
            this.putData("chat_approved", "true");
        }
    }
    public IChannel discord(){
        return Inquisitor.discordClient().getChannelByID(this.getID());
    }
    public boolean isPrivate(){
        return discord() instanceof IPrivateChannel;
    }
}
