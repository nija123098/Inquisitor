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
    private final String name;
    private final Method method;
    private final Register register;
    public Command(Method method) {
        this.method = method;
        this.register = method.getAnnotation(Register.class);
        String name = this.register.name().toLowerCase();
        if (name.equals("")){
            if (this.natural()){
                this.name = this.method.getName().toLowerCase();
            }else if (this.defaul()) {
                this.name = this.method.getDeclaringClass().getSimpleName().toLowerCase();
            }else{
                this.name = this.method.getDeclaringClass().getSimpleName().toLowerCase() + " " + this.method.getName().toLowerCase();
            }
        }else{
            this.name = name;
        }
    }
    public String help(){
        return this.register.help();
    }
    public String name(){
        return this.name;
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
    public boolean rankSufficient(Rank rank){
        return rank.ordinal() >= this.rank().ordinal();
    }
    public boolean startup() {
        return this.register.startup();
    }
    public boolean guild(){
        return this.register.guild();
    }
    public boolean hidden(){
        return this.register.hidden();
    }
    public float suspicious(){
        return this.register.suspicious();
    }
    public Suspicion suspicion(){
        return this.register.suspicion();
    }
    public boolean invoke(User user, Guild guild, Channel channel, String s){
        Rank rank = Rank.getRank(user, guild);
        if (!this.rankSufficient(rank)){
            MessageHelper.send(user, "That command is above your rank");
            return false;
        }
        if (this.guild() && guild == null){
            MessageHelper.send(user, "That command can not be used in a private channel");
            return false;
        }
        if (Suspicion.getLevel(user).ordinal() < this.suspicion().ordinal()){
            MessageHelper.send(channel, user.discord().mention() + " you are " + Suspicion.getLevel(user).name() + ", you can not use that command");
            return false;
        }
        Object[] objects = new Object[this.method.getParameterTypes().length];
        Class[] parameterTypes = this.method.getParameterTypes();
        for (int i = 0; i < objects.length; i++) {
            if (parameterTypes[i].equals(User.class)){
                objects[i] = user;
            }else if (parameterTypes[i].equals(Guild.class)){
                objects[i] = guild;
            }else if (parameterTypes[i].equals(Channel.class)){
                objects[i] = channel;
            }else if (parameterTypes[i].equals(String.class)){
                objects[i] = s;
            }else if (parameterTypes[i].equals(String[].class)){
                String[] strings = s.split(" ");
                if (strings[0].equals("")){
                    objects[i] = new String[0];
                }else{
                    objects[i] = strings;
                }
            }else if (parameterTypes[i].equals(Command.class)){
                objects[i] = this;
            }else if (parameterTypes[i].equals(Rank.class)){
                objects[i] = rank;
            }
        }
        try {
            if (objects.length == 0){
                this.method.invoke(null);
            }else{
                this.method.invoke(null, objects);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.error(this.method.getDeclaringClass().getName() + "#" + this.method.getName() + " ran into a " + e.getClass().getSimpleName() + " and got " + e.getMessage() + " while being invoked");
            e.printStackTrace();
            return false;
        }
        float level = Float.parseFloat(user.getData("suspicion", "0"));
        Suspicion suspicion = Suspicion.getLevel(level);
        float newLevel = level + this.suspicious();
        user.putData("suspicion", newLevel + "");
        Suspicion newSuspicion = Suspicion.getLevel(newLevel);
        if (suspicion != newSuspicion){
            MessageHelper.send(channel, user.discord().mention() + " you are now considered " + newSuspicion);
        }
        return true;
    }
}
