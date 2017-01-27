package um.nija123098.inquisitor.bot;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
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
import um.nija123098.inquisitor.util.FileHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.ArrayList;
import java.util.List;
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
    public static boolean getLockdown(){
        return inquisitor.lockdown;
    }
    public static void lockdown(){
        inquisitor.lockdown = true;
    }// restart required to unlock Inquisitor is on purpose
    public static Entity getEntity(String name){
        return inquisitor.getEnt(name);
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
    private IDiscordClient client;
    private Inquisitor(String token){
        this.botList = new ArrayList<>(1);
        RequestHandler.request(() -> {
            this.client = new ClientBuilder().withToken(token).build();
            this.client.getDispatcher().registerListener(this);
            RequestHandler.request(() -> this.client.login());
        });
    }
    @EventSubscriber
    public void handle(GuildCreateEvent event){
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
    public void handle(DisconnectedEvent event){
        if (event.getReason().equals(DisconnectedEvent.Reason.LOGGED_OUT)){
            try{Thread.sleep(1000);
            }catch(InterruptedException e){e.printStackTrace();}
            System.exit(11);
        }
    }
    public Entity getEnt(String name){
        return Entity.getEntity(FileHelper.getJarContainer() + "\\system", name);
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
        try {
            this.botList.forEach(GuildBot::close);
            RequestHandler.request(() -> this.client.logout());
            Log.info("Shutting down");
        }catch (Exception e){
            Log.warn("Failed to shut down");
            e.printStackTrace();
        }
    }
}
