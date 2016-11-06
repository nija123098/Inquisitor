package um.nija123098.inquisitor.util;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Made by nija123098 on 11/6/2016
 */
public class ContextHelper {
    public static boolean isAdmin(IUser user, IGuild guild){
        if (guild.getOwner().equals(user) || user.getID().equals("191677220027236352")){
            return true;
        }
        for (IRole iRole : user.getRolesForGuild(guild)){
            if (iRole.getPermissions().contains(Permissions.ADMINISTRATOR)){
                return true;
            }
        }
        return false;
    }
    public static void execute(Method method, boolean admin, User user, Guild guild, Channel channel, String s){
        Object[] objects = new Object[method.getParameterTypes().length];
        for (int i = 0; i < objects.length; i++) {
            if (method.getParameterTypes()[i].equals(Boolean.class)){
                objects[i] = admin;
            }else if (method.getParameterTypes()[i].equals(User.class)){
                objects[i] = user;
            }else if (method.getParameterTypes()[i].equals(Guild.class)){
                objects[i] = guild;
            }else if (method.getParameterTypes()[i].equals(Channel.class)){
                objects[i] = channel;
            }else if (method.getParameterTypes()[i].equals(String.class)){
                objects[i] = s;
            }
        }
        try {
            if (objects.length == 0){
                method.invoke(null);
            }else{
                method.invoke(null, objects);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.error(method.getDeclaringClass().getName() + "#" + method.getName() + " ran into a " + e.getClass().getSimpleName() + " and got " + e.getMessage());
        }
    }
}
