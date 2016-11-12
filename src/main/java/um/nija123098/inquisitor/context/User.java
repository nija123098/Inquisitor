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
    public User(String id) {
        super("user", id);
    }
    public IUser discord(){
        return Inquisitor.discordClient().getUserByID(this.getID());
    }
}
