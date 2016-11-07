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
        FileHelper.getFiles("channels").forEach(file -> {
            try{CHANNELS.add(new Channel(file.getName(), FileHelper.getStringsNoAdjust(file.getPath())));
            }catch(Exception ignored){}
        });
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
    private volatile boolean approved;
    public Channel(String id) {
        super(id);
        this.approved = this.discordChannel().getName().toLowerCase().contains("test") || discordChannel() instanceof IPrivateChannel;
    }
    public Channel(String id, List<String> strings){
        this(id);
        this.approved = Boolean.parseBoolean(strings.get(0));
    }
    public List<String> getStrings(){
        List<String> strings = new ArrayList<String>();
        strings.add(this.approved + "");
        return strings;
    }
    public IChannel discordChannel(){
        return Inquisitor.inquisitor().getClient().getChannelByID(this.getID());
    }
    public boolean chatApproved(){
        return this.approved;
    }
    public void approveChat(boolean approved){
        this.approved = approved;
    }
}
