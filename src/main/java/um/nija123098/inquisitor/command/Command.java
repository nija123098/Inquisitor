package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Made by nija123098 on 11/7/2016
 */
public class Command {
    private final Method method;
    private final Register register;
    public Command(Method method) {
        this.method = method;
        this.register = method.getAnnotation(Register.class);
    }
    public String help(){
        return this.register.help();
    }
    public String name(){
        String name = this.register.name();
        if (name.equals("")){
            if (this.natural()){
                return this.method.getName();
            }else{
                return this.method.getDeclaringClass().getName().toLowerCase() + " " + this.method.getName().toLowerCase();
            }
        }
        return name;
    }
    public boolean natural(){
        return this.register.natural();
    }
    public boolean surface(){
        return this.register.natural() || this.register.defaul();
    }
    public boolean defaul(){
        return this.register.defaul();
    }
    public Rank rank(){
        return this.register.rank();
    }
    public boolean runOnStartUp() {
        return this.register.runOnStartup();
    }
    public boolean invoke(User user, Guild guild, Channel channel, String s){
        if (Rank.getRank(user, guild).ordinal() < this.rank().ordinal()){
            MessageHelper.send(user, "That method is above your rank");
            return false;
        }
        Object[] objects = new Object[this.method.getParameterTypes().length];
        for (int i = 0; i < objects.length; i++) {
            if (this.method.getParameterTypes()[i].equals(User.class)){
                objects[i] = user;
            }else if (this.method.getParameterTypes()[i].equals(Guild.class)){
                objects[i] = guild;
            }else if (this.method.getParameterTypes()[i].equals(Channel.class)){
                objects[i] = channel;
            }else if (this.method.getParameterTypes()[i].equals(String.class)){
                objects[i] = s;
            }else if (this.method.getParameterTypes()[i].equals(Command.class)){
                objects[i] = this;
            }
        }
        try {
            if (objects.length == 0){
                this.method.invoke(null);
            }else{
                this.method.invoke(null, objects);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.error(this.method.getDeclaringClass().getName() + "#" + this.method.getName() + " ran into a " + e.getClass().getSimpleName() + " and got " + e.getMessage());
            return false;
        }
        return true;
    }
}
