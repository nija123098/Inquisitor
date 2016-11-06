package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.IChannel;
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
        FileHelper.getFiles("channels").forEach(file -> CHANNELS.add(new Channel(file.getName(), FileHelper.getStrings(file.getPath()))));
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
    public static void save(){
        FileHelper.cleanDir("channels");
        CHANNELS.forEach(channel -> FileHelper.writeStrings("channels\\" + channel.getID(), channel.getStrings()));
    }
    public Channel(String id) {
        super(id);
    }
    public Channel(String id, List<String> strings){
        this(id);
    }
    public List<String> getStrings(){
        return new ArrayList<String>();
    }
    public IChannel discordChannel(){
        return Inquisitor.inquisitor().getClient().getChannelByID(this.getID());
    }
}
