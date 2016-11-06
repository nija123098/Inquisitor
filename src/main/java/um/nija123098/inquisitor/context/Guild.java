package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.IGuild;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.util.FileHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Guild extends Context {
    private static final List<Guild> GUILDS;
    static {
        GUILDS = new ArrayList<Guild>();
        FileHelper.ensureFileExistence("guilds");
        FileHelper.getFiles("guilds").forEach(file -> GUILDS.add(new Guild(file.getName(), FileHelper.getStrings("guilds\\" + file.getName()))));
    }
    public static Guild getGuild(String id){
        for (Guild guild : GUILDS) {
            if (guild.getID().equals(id)){
                return guild;
            }
        }
        Guild guild = new Guild(id);
        GUILDS.add(guild);
        return guild;
    }
    public static void save(){
        FileHelper.cleanDir("guilds");
        GUILDS.forEach(channel -> FileHelper.writeStrings("guilds\\" + channel.getID(), channel.getStrings()));
    }
    private volatile String prefix;
    public Guild(String id) {
        super(id);
        this.prefix = "-";
    }
    public Guild(String id, List<String> strings) {
        this(id);
        this.prefix = strings.get(0);
    }
    public String getPrefix(){
        return this.prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public List<String> getStrings(){
        List<String> strings = new ArrayList<String>(1);
        strings.add(this.prefix);
        return strings;
    }
    public IGuild discordGuild(){
        return Inquisitor.inquisitor().getClient().getGuildByID(this.getID());
    }
}
