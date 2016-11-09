package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.util.ClassFinder;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Inquisitor {
    private static Inquisitor inquisitor;
    public static Inquisitor inquisitor(){
        return inquisitor;
    }
    public static void main(String[] args) {
        List<Class<?>> classes = ClassFinder.find("um.nija123098.inquisitor.commands");
        classes.forEach(Registry::register);
        inquisitor = new Inquisitor(args[0]);
    }
    private final List<GuildBot> botList;
    private IDiscordClient client;
    public Inquisitor(String token){
        this.botList = new ArrayList<GuildBot>(1);
        RequestHandler.request(() -> {
            this.client = new ClientBuilder().withToken(token).build();
            RequestHandler.request(() -> {
                this.client.login();
                this.client.getDispatcher().registerListener(this);
            });
        });
    }
    @EventSubscriber
    public void handle(GuildCreateEvent event){
        this.botList.add(new GuildBot(this.client, event.getGuild().getID()));
    }
    @EventSubscriber
    public void handle(MessageReceivedEvent event){
        if (event.getMessage().getChannel() instanceof IPrivateChannel){
            Invoke.invoke(event.getMessage().getAuthor().getID(), null, event.getMessage().getChannel().getID(), event.getMessage().getContent());
        }
    }
    @EventSubscriber
    public void handle(ReadyEvent event){
        Registry.startUp();
    }
    public IDiscordClient getClient(){
        return this.client;
    }
    public void close(){
        this.botList.forEach(GuildBot::close);
    }
}
