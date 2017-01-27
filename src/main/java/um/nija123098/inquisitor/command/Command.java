package um.nija123098.inquisitor.command;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.*;
import um.nija123098.inquisitor.util.FileHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

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
    private final Entity entity;
    public Command(Method method) {
        this.method = method;
        this.register = method.getAnnotation(Register.class);
        this.clazz = !method.getDeclaringClass().isAnnotationPresent(Register.class) || this.register.override() ? DEFAULT : method.getDeclaringClass().getAnnotation(Register.class);
        String name = this.register.name().toLowerCase();
        if (name.equals("")){
            if (this.natural()){
                name = this.method.getName();
            }else if (this.defaul()) {
                name = this.clazz.name().length() == 0 ? this.method.getDeclaringClass().getSimpleName() : this.clazz.name();
            }else{
                name = (this.clazz.name().length() == 0 ? this.method.getDeclaringClass().getSimpleName() : this.clazz.name()) + " " + this.method.getName();
            }
        }
        this.name = name.toLowerCase();
        Entity ent = null;
        for (Class clazz : this.method.getParameterTypes()) {
            if (clazz.equals(Entity.class)){
                ent = Entity.getEntity(FileHelper.getJarContainer() + "\\" + "command", this.name.split(" ")[0]);
                break;
            }
        }
        if (ent == null){
            this.entity = null;
        }else{
            this.entity = ent;
        }
    }
    public String help(){
        if (!DEFAULT.help().equals(this.clazz.help())){
            return this.clazz.help();
        }
        return this.register.help().length() == 0 ? "Help not supported" : this.register.help();
    }
    public String name(){
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
    public boolean override(){
        return this.register.override();
    }
    public boolean invoke(User user, Guild guild, Channel channel, String s, IMessage message){
        Rank rank = null;
        Suspicion suspicion = null;
        if (!this.startup() && !this.shutdown() && user != null){
            rank = Rank.getRank(user, guild);
            if (Inquisitor.getLockdown() && !Rank.isSufficient(Rank.BOT_ADMIN, rank)){
                MessageHelper.send(channel, Inquisitor.ourUser().mention() + " is currently on lockdown");
                return false;
            }
            if (!this.rankSufficient(rank)){
                MessageHelper.send(user, "That command is above your rank");
                return false;
            }
            if (guild == null){
                if (this.guild()){
                    MessageHelper.send(user, "That command can not be used in a private channel");
                    return false;
                }
            }else{
                if (guild.getData("blacklist", "").contains(this.name())){
                    MessageHelper.send(user, "That command has been blacklisted for " + guild.discord().getName());
                    return false;
                }
            }
            suspicion = Suspicion.getLevel(user);
            if (Suspicion.isSufficient(this.suspicion(), suspicion)){
                MessageHelper.send(channel, user.discord().mention() + " you are " + Suspicion.getLevel(user).name() + ", you can not use that command");
                return false;
            }
        }
        MessageAid aider = null;
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
                objects[i] = s.length() == 0 ? new String[0] : s.split(" ");
            }else if (parameterTypes[i].equals(Command.class)){
                objects[i] = this;
            }else if (parameterTypes[i].equals(Rank.class)){
                objects[i] = rank;
            }else if (parameterTypes[i].equals(Suspicion.class)){
                objects[i] = suspicion;
            }else if (parameterTypes[i].equals(Entity.class)){
                objects[i] = this.entity;
            }else if (parameterTypes[i].equals(IMessage.class)){
                objects[i] = message;
            }else if (parameterTypes[i].equals(IVoiceChannel.class)){
                if (user == null){
                    objects[i] = null;
                }else{
                    for (IVoiceChannel vChannel : user.discord().getConnectedVoiceChannels()) {
                        if (vChannel.getGuild().equals(guild.discord())){
                            objects[i] = channel;
                        }
                    }
                    objects[i] = channel;
                }
            }else if (parameterTypes[i].equals(MessageAid.class)){
                if (aider == null){
                    aider = new MessageAid(user, channel, guild);
                }
                objects[i] = aider;
            }
        }
        Object ret;
        try {
            if (objects.length == 0){
                ret = this.method.invoke(null);
            }else{
                ret = this.method.invoke(null, objects);
            }
        } catch (IllegalAccessException e){
            return false;
        } catch (InvocationTargetException e) {
            Log.error(this.method.getDeclaringClass().getName() + "#" + this.method.getName() + " ran into a " + e.getClass().getSimpleName() + " and got " + e.getMessage() + " while being invoked by " + (user == null ? "the system" : user.discord().getName() + "#" + user.discord().getDiscriminator()));
            e.printStackTrace();
            return false;
        }
        if (aider != null){
            aider.send();
        }
        if (!this.startup() && !this.shutdown() && user != null && (ret == null || Objects.equals(true, ret))){
            Suspicion.addLevel(user, this.suspicious(), channel, true);
            if (this.suspicious() > 0){
                MessageHelper.react("eye", message);
            }
        }
        return ret == null || Objects.equals(true, ret);
    }
}
