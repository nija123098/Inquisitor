package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.bot.Entity;
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
@Register
public class Command {
    private static final Register DEFAULT;
    static {
        DEFAULT = Command.class.getAnnotation(Register.class);
    }
    private final String name;
    private final Method method;
    private final Register register, clazz;
    public Command(Method method) {
        this.method = method;
        this.register = method.getAnnotation(Register.class);
        this.clazz = method.getDeclaringClass().getAnnotation(Register.class) == null ? DEFAULT : method.getDeclaringClass().getAnnotation(Register.class);
        String name = this.register.name().toLowerCase();
        if (name.equals("")){
            if (this.natural()){
                this.name = this.method.getName().toLowerCase();
            }else if (this.defaul()) {
                this.name = this.method.getDeclaringClass().isAnnotationPresent(ClassName.class) ? this.method.getDeclaringClass().getAnnotation(ClassName.class).value() : this.method.getDeclaringClass().getSimpleName().toLowerCase();
            }else{
                this.name = (this.method.getDeclaringClass().isAnnotationPresent(ClassName.class) ? this.method.getDeclaringClass().getAnnotation(ClassName.class).value() : this.method.getDeclaringClass().getSimpleName().toLowerCase()) + " " + this.method.getName().toLowerCase();
            }
        }else{
            this.name = name;
        }
    }
    public String help(){
        if (!DEFAULT.help().equals(this.clazz.help())){
            return this.clazz.help();
        }
        return this.register.help().length() == 0 ? "Help not supported" : this.register.help();
    }
    public String name(){
        if (!DEFAULT.name().equals(this.clazz.name())){
            return this.clazz.name();
        }
        return this.name;
    }
    public boolean natural(){
        if (DEFAULT.natural() != this.clazz.natural()){
            return this.clazz.natural();
        }
        return this.register.natural();
    }
    public boolean surface(){
        return this.natural() || this.defaul();
    }
    public boolean defaul(){
        if (DEFAULT.defaul() != this.clazz.defaul()){
            return this.clazz.defaul();
        }
        return this.register.defaul();
    }
    public Rank rank(){
        if (DEFAULT.rank() != this.clazz.rank()){
            return this.clazz.rank();
        }
        return this.register.rank();
    }
    public boolean rankSufficient(Rank rank){
        return Rank.isSufficient(this.rank(), rank);
    }
    public boolean startup() {
        if (DEFAULT.startup() != this.clazz.startup()){
            return this.clazz.startup();
        }
        return this.register.startup();
    }
    public boolean shutdown() {
        if (DEFAULT.shutdown() != this.clazz.shutdown()){
            return this.clazz.shutdown();
        }
        return this.register.shutdown();
    }
    public boolean guild(){
        if (DEFAULT.guild() != this.clazz.guild()){
            return this.clazz.guild();
        }
        return this.register.guild();
    }
    public boolean hidden(){
        if (DEFAULT.hidden() != this.clazz.hidden()){
            return this.clazz.hidden();
        }
        return this.register.hidden();
    }
    public float suspicious(){
        if (DEFAULT.suspicious() != this.clazz.suspicious()){
            return this.clazz.suspicious();
        }
        return this.register.suspicious();
    }
    public Suspicion suspicion(){
        if (DEFAULT.suspicion() != this.clazz.suspicion()){
            return this.clazz.suspicion();
        }
        return this.register.suspicion();
    }
    public boolean args() {
        if (DEFAULT.args() != this.clazz.args()){
            return this.clazz.args();
        }
        return this.register.args();
    }
    public boolean invoke(User user, Guild guild, Channel channel, String s){
        Rank rank = Rank.NONE;
        Suspicion suspicion = Suspicion.ENLIGHTENED;
        if (!this.startup() && !this.shutdown()){
            rank = Rank.getRank(user, guild);
            if (!this.rankSufficient(rank)){
                MessageHelper.send(user, "That command is above your rank");
                return false;
            }
            if (this.guild() && guild == null){
                MessageHelper.send(user, "That command can not be used in a private channel");
                return false;
            }
            suspicion = Suspicion.getLevel(user);
            if (suspicion.ordinal() < this.suspicion().ordinal()){
                MessageHelper.send(channel, user.discord().mention() + " you are " + Suspicion.getLevel(user).name() + ", you can not use that command");
                return false;
            }
        }
        Class[] parameterTypes = this.method.getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
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
                objects[i] = strings[0].equals("") ? new String[0] : strings;
            }else if (parameterTypes[i].equals(Command.class)){
                objects[i] = this;
            }else if (parameterTypes[i].equals(Rank.class)){
                objects[i] = rank;
            }else if (parameterTypes[i].equals(Suspicion.class)){
                objects[i] = suspicion;
            }else if (parameterTypes[i].equals(Entity.class)){
                objects[i] = Entity.getEntity("command", this.name.split(" ")[0]);
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
        if (!this.startup() && !this.shutdown()){
            Suspicion.addLevel(user, this.suspicious(), channel);
        }
        return true;
    }
}
