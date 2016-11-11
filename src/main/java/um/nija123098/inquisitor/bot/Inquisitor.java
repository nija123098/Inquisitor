package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.ClassFinder;
import um.nija123098.inquisitor.util.FileHelper;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Inquisitor {
    private static Inquisitor inquisitor;
    public static IDiscordClient discordClient(){
        return inquisitor.getClient();
    }
    public static Entity getEntity(String name){
        return inquisitor.getEnt(name);
    }
    public static void close(){
        inquisitor.closeInner();
    }
    public static void save(){
        inquisitor.saveInner();
    }
    public static void main(String[] args) {
        List<Class<?>> classes = ClassFinder.find("um.nija123098.inquisitor.commands");
        classes.forEach(Registry::register);
        inquisitor = new Inquisitor(args[0]);
    }
    private final List<GuildBot> botList;
    private final List<Entity> entities;
    private IDiscordClient client;
    private Inquisitor(String token){
        this.entities = new ArrayList<Entity>();
        this.botList = new ArrayList<GuildBot>(1);
        RequestHandler.request(() -> {
            this.client = new ClientBuilder().withToken(token).build();
            RequestHandler.request(() -> {
                this.client.login();
                this.client.getDispatcher().registerListener(this);
            });
        });
        FileHelper.getFiles("entities").forEach(file -> this.entities.add(new Entity(file.getName(), FileHelper.getStringsNoAdjust(file.getPath()))));
    }
    @EventSubscriber
    public void handle(GuildCreateEvent event){
        synchronized (this.botList){
            for (GuildBot guildBot : this.botList) {
                if (guildBot.guildID().equals(event.getGuild().getID())){
                    return;
                }
            }
            this.botList.add(new GuildBot(this.client, event.getGuild().getID()));
        }
    }
    @EventSubscriber
    public void handle(MessageReceivedEvent event){
        if (event.getMessage().getChannel() instanceof IPrivateChannel){
            Invoke.invoke(event.getMessage().getAuthor().getID(), null, event.getMessage().getChannel().getID(), event.getMessage().getContent());
        }
    }
    @EventSubscriber
    public void handle(ReadyEvent event){
        synchronized (this.botList){
            event.getClient().getGuilds().forEach(guild -> this.botList.add(new GuildBot(this.client, guild.getID())));
        }
        Registry.startUp();
    }
    @EventSubscriber
    public void handle(DiscordDisconnectedEvent event){
        if (event.getReason().equals(DiscordDisconnectedEvent.Reason.LOGGED_OUT)){
            System.exit(11);
        }
    }
    public Entity getEnt(String name){
        for (Entity ent : this.entities) {
            if (ent.name().equals(name)){
                return ent;
            }
        }
        Entity entity = new Entity(name);
        this.entities.add(entity);
        return entity;
    }
    public IDiscordClient getClient(){
        return this.client;
    }
    public void saveInner(){
        User.save();
        Channel.save();
        Guild.save();
        FileHelper.cleanDir("entities");
        this.entities.forEach(entity -> FileHelper.writeStrings("entities\\" + entity.name(), entity.getStrings()));
    }
    public void closeInner(){
        this.botList.forEach(GuildBot::close);
        this.saveInner();
        RequestHandler.request(() -> this.client.logout());
    }
}
