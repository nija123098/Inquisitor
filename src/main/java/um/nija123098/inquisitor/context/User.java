package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.IUser;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.util.FileHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class User extends Context {
    private static final List<User> USERS;
    static {
        USERS = new ArrayList<User>();
        FileHelper.ensureFileExistence("users");
        FileHelper.getFiles("users").forEach(file -> {
            try{USERS.add(new User(file.getName(), FileHelper.getStringsNoAdjust(file.getPath())));
            }catch(Exception ignored){}
        });
    }
    public static User getUser(String id){
        for (User user : USERS) {
            if (user.getID().equals(id)){
                return user;
            }
        }
        User user = new User(id);
        USERS.add(user);
        return user;
    }
    public static void save(){
        FileHelper.cleanDir("users");
        USERS.forEach(channel -> FileHelper.writeStrings("users\\" + channel.getID(), channel.getStrings()));
    }
    public User(String id) {
        super(id);
    }
    public User(String id, List<String> strings) {
        this(id);
    }
    public List<String> getStrings(){
        return new ArrayList<String>();
    }
    public IUser discordUser(){
        return Inquisitor.inquisitor().getClient().getUserByID(this.getID());
    }
}
