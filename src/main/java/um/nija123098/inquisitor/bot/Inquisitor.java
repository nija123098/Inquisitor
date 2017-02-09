package um.nija123098.inquisitor.bot;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.util.ClassFinder;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Inquisitor {
    public static void main(String[] args) {
        Discord4J.disableChannelWarnings();
        inquisitor = new Inquisitor(args[0]);
        Runtime.getRuntime().addShutdownHook(new Thread(Inquisitor::save, "Shutdown Hook"));
        Registry.register(ClassFinder.find("um.nija123098.inquisitor.commands").stream().collect(Collectors.toList()));
        Log.info("Command registration complete");
    }
    private static Inquisitor inquisitor;
    public static IDiscordClient discordClient(){
        return inquisitor.getClient();
    }
    public static IUser ourUser(){
        return inquisitor.getClient().getOurUser();
    }
    private static int exitCode;
    public static void setExitCode(int code){
        exitCode = code;
    }
    public static boolean getLockdown(){
        return inquisitor.lockdown;
    }
    public static void lockdown(){
        inquisitor.lockdown = true;
    }// restart required to unlock Inquisitor is on purpose
    public static Entity getEntity(String name){
        return inquisitor.getEnt(name);
    }
    public static void registerListener(Object o){
        inquisitor.innerRegisterListener(o);
    }
    public static void unregisterListener(Object o){
        inquisitor.innerUnregisterListener(o);
    }
    public static void close(){
        inquisitor.closeInner();
    }
    public static void save(){
        inquisitor.saveInner();
    }
    public static String mention(){
        return inquisitor.client.getOurUser().mention(true);
    }
    private volatile boolean lockdown;
    private final List<GuildBot> botList;
    private final List<Object> listeners;
    private IDiscordClient client;
    private Inquisitor(String token){
        this.botList = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        RequestHandler.request(() -> {
            this.client = new ClientBuilder().withToken(token).build();
            registerListener(this);
            RequestHandler.request(() -> this.client.login());
        });
    }
    @EventSubscriber
    public void handle(GuildCreateEvent event){
        int botCount = event.getGuild().getUsers().stream().filter(IUser::isBot).collect(Collectors.toList()).size();
        if (botCount > event.getGuild().getUsers().size() / 2){
            event.getGuild().leave();
        }
        this.botList.add(new GuildBot(this.client, event.getGuild().getID()));
    }
    @EventSubscriber
    public void handle(ReadyEvent event){
        Registry.startUp();
        Log.info("Commands startup complete");
    }
    @EventSubscriber
    public void handle(MessageReceivedEvent event){
        String s = event.getMessage().getContent();
        if (s != null && event.getMessage().getChannel() instanceof IPrivateChannel){
            Invoke.invoke(event.getMessage().getAuthor().getID(), null, event.getMessage().getChannel().getID(), s.equals("?") ? "help" : s, event.getMessage());
        }
    }
    @EventSubscriber
    public void handle(ReactionAddEvent event){
        Invoke.invoke(event.getUser().getID(), event.getGuild() == null ? null : event.getGuild().getID(), event.getChannel().getID(), event.getMessage().getContent(), event.getReaction());
    }
    @EventSubscriber
    public void handle(DisconnectedEvent event){
        if (event.getReason().equals(DisconnectedEvent.Reason.LOGGED_OUT)){
            this.listeners.forEach(this::innerUnregisterListener);
            if (exitCode == 0){
                RequestHandler.turnOff();
            }
            try{Thread.sleep(1000);
            }catch(InterruptedException e){e.printStackTrace();}
            System.exit(exitCode);
        }
    }
    public void innerRegisterListener(Object o){
        if (o instanceof Class<?>){
            this.client.getDispatcher().registerListener(((Class<?>) o));
        }else if (o instanceof IListener<?>){
            this.client.getDispatcher().registerListener(((IListener<?>) o));
        }else{
            this.client.getDispatcher().registerListener(o);
        }
        synchronized (this.listeners){
            this.listeners.add(o);
        }
    }
    public void innerUnregisterListener(Object o){
        synchronized (this.listeners){
            if (!this.listeners.remove(o)){
                Log.warn("Attempted to unregister not registered Object: " + o.toString());
                return;
            }
        }
        if (o instanceof Class<?>){
            this.client.getDispatcher().unregisterListener(((Class<?>) o));
        }else if (o instanceof IListener<?>){
            this.client.getDispatcher().unregisterListener(((IListener<?>) o));
        }else{
            this.client.getDispatcher().unregisterListener(o);
        }
    }
    public Entity getEnt(String name){
        return Entity.getEntity("system", name);
    }
    public IDiscordClient getClient(){
        return this.client;
    }
    public void saveInner(){
        Log.info("Saving");
        Entity.saveEntities();
    }
    public void closeInner(){
        Registry.shutDown();
        saveInner();
        try {
            this.botList.forEach(GuildBot::close);
            RequestHandler.turnOff();
            RequestHandler.request(() -> this.client.logout());
            Log.info("Shutting down");
        }catch (Exception e){
            Log.warn("Failed to shut down");
            e.printStackTrace();
        }
    }
}
