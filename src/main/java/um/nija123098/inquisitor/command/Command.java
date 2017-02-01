package um.nija123098.inquisitor.command;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IVoiceChannel;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.*;
import um.nija123098.inquisitor.util.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final List<String> aliases, reactionAliases, names;
    private final String name;
    private final Method method;
    private final Register register, clazz;
    private final Entity entity;
    Command(Method method, List<Method> others) {
        this.method = method;
        this.register = method.getAnnotation(Register.class);
        if (this.register.supercommand().length() == 0){
            this.clazz = !method.getDeclaringClass().isAnnotationPresent(Register.class) || this.register.override() ? DEFAULT : method.getDeclaringClass().getAnnotation(Register.class);
        }else{
            Register reg = null;
            for (Method m : others){
                if ((m.getClass().getSimpleName() + "#" + m.getName()).toLowerCase().equals(this.register.supercommand())){
                    reg = m.getDeclaredAnnotation(Register.class);
                    break;
                }
            }
            this.clazz = reg;
        }
        String className = this.clazz.name().length() == 0 ? this.method.getDeclaringClass().getSimpleName() : this.clazz.name();
        String name = this.register.name();
        ArrayList<String> absoluteAliases = new ArrayList<>();
        Collections.addAll(absoluteAliases, this.register.absoluteAliases().toLowerCase().split(", "));
        this.reactionAliases = new ArrayList<>();
        ArrayList<String> rea = new ArrayList<>();
        Collections.addAll(rea, this.register.emoticonAliases().split(", "));
        if (!rea.get(0).equals("")){
            rea.forEach(s -> this.reactionAliases.add(EmoticonHelper.getEmoticon(s)));
            this.reactionAliases.forEach(System.out::println);
        }
        this.aliases = new ArrayList<>();
        if (name.equals("")){
            if (this.natural()){
                name = this.method.getName();
            }else if (this.defaul()) {
                name = className;
            }else{
                name = (className) + " " + this.method.getName();
                ArrayList<String> unadjustedAliases = new ArrayList<>();
                Collections.addAll(unadjustedAliases, this.register.aliases().split(", "));
                unadjustedAliases.stream().filter(s -> s.length() != 0).forEach(s -> this.aliases.add((className + " " + s).toLowerCase()));
            }
            if (this.aliases.size() == 0 && this.register.aliases().length() != 0){
                Collections.addAll(this.aliases, this.register.aliases().toLowerCase().split(", "));
            }
        }
        if (absoluteAliases.get(0).length() != 0){
            this.aliases.addAll(absoluteAliases);
        }
        this.name = name.toLowerCase();
        this.names = new ArrayList<>();
        this.names.add(this.name);
        this.names.addAll(this.aliases);
        this.names.addAll(this.reactionAliases);
        Entity ent = null;
        for (Class clazz : this.method.getParameterTypes()) {
            if (clazz.equals(Entity.class)){
                ent = Entity.getEntity("command", this.name.split(" ")[0]);
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
    public List<String> aliases(){
        return this.aliases;
    }
    public List<String> reactionAliases(){
        return this.reactionAliases;
    }
    public List<String> names(){
        return this.names;
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
    public boolean override(){
        return this.register.override();
    }
    public boolean invoke(User user, Guild guild, Channel channel, String s, IMessage message, IReaction reaction, Boolean warn){
        Rank rank = null;
        Suspicion suspicion = null;
        if (!this.startup() && !this.shutdown() && user != null){
            rank = Rank.getRank(user, guild);
            if (Inquisitor.getLockdown() && !Rank.isSufficient(Rank.BOT_ADMIN, rank)){
                if (warn){
                    MessageHelper.send(channel, Inquisitor.ourUser().mention() + " is currently on lockdown");
                }
                return false;
            }
            if (!this.rankSufficient(rank)){
                if (warn){
                    MessageHelper.send(user, "That command is above your rank");
                }
                return false;
            }
            if (guild == null){
                if (this.guild()){
                    if (warn){
                        MessageHelper.send(user, "That command can not be used in a private channel");
                    }
                    return false;
                }
            }else{
                if (guild.getData("blacklist", "").contains(this.name())){
                    if (warn){
                        MessageHelper.send(user, "That command has been blacklisted for " + guild.discord().getName());
                    }
                    return false;
                }
            }
            suspicion = Suspicion.getLevel(user);
            if (Suspicion.isSufficient(this.suspicion(), suspicion)){
                if (warn){
                    MessageHelper.send(channel, user.discord().mention() + " you are " + Suspicion.getLevel(user).name() + ", you can not use that command");
                }
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
                            objects[i] = vChannel;
                        }
                    }
                    objects[i] = channel;
                }
            }else if (parameterTypes[i].equals(MessageAid.class)){
                if (aider == null){
                    aider = new MessageAid(user, channel, guild);
                }
                objects[i] = aider;
            }else if (parameterTypes[i].equals(IReaction.class)){
                objects[i] = reaction;
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
