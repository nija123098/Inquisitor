package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.IUser;
import um.nija123098.inquisitor.bot.Inquisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class User extends Context {
    private static final List<User> USERS;
    static {
        USERS = new ArrayList<User>();
    }
    public static User getUserFromID(String id){
        try{Long.parseLong(id);
        }catch(Exception e){return null;}
        synchronized (USERS){
            for (User user : USERS) {
                if (user.getID().equals(id)){
                    return user;
                }
            }
        }
        User user = new User(id);
        synchronized (USERS){
            USERS.add(user);
        }
        return user;
    }
    public static User getUser(String s){
        return getUser(s, null);
    }
    public static User getUser(String s, Guild guild){
        if (s.contains("<@") && s.contains(">")){
            String id = s.replace("<@", "").replace("!", "").replace(">", "");
            synchronized (USERS){
                for (User user : USERS) {
                    if (user.getID().equals(id)){
                        return user;
                    }
                }
            }
        }
        String undiscrim = s;
        String discrim = null;
        if (undiscrim.contains("#")){
            undiscrim = s.substring(0, s.indexOf("#"));
            discrim = s.substring(s.indexOf("#") + 1);
        }
        User user = null;
        if (guild != null){
            user = getUserFromList(discrim, undiscrim, guild.discord().getUsers());
        }
        if (user != null){
            return user;
        }
        user = getUserFromList(discrim, undiscrim, Inquisitor.discordClient().getUsers());
        return user;
    }
    private static User getUserFromList(String discrim, String undiscrim, List<IUser> iUsers){
        List<IUser> users = new ArrayList<IUser>();
        for (IUser user : iUsers) {
            if (user.getName().equals(undiscrim)){
                users.add(user);
            }
        }
        if (users.size() == 1){
            return getUserFromID(users.get(0).getID());
        }else if (users.size() > 1 && discrim != null){
            for (IUser user : users) {
                if (discrim.equals(user.getDiscriminator())){
                    return getUserFromID(user.getID());
                }
            }
        }
        return null;
    }
    public User(String id) {
        super("user", id);
    }
    public IUser discord(){
        return Inquisitor.discordClient().getUserByID(this.getID());
    }
    @Deprecated
    public String getData(String id) {
        return super.getData(id);
    }
    @Deprecated
    public String getData(String id, String defaul) {
        return super.getData(id, defaul);
    }
    @Deprecated
    public void putData(String id, String data) {
        super.putData(id, data);
    }
    @Deprecated
    public void clearData(String id){
        super.clearData(id);
    }
}
