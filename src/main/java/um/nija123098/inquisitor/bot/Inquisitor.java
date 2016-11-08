package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.command.Execute;
import um.nija123098.inquisitor.command.Registry;
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
    public static Inquisitor inquisitor(){
        return inquisitor;
    }
    public static void main(String[] args) {
        List<Class<?>> classes = ClassFinder.find("um.nija123098.inquisitor.register");
        classes.forEach(clazz -> {
            Registry.registerCommand(clazz);
            Registry.registerListener(clazz);
            Registry.registerStarter(clazz);
            Registry.registerTimerTask(clazz);
            Registry.registerGuildOpenings(clazz);
        });
        inquisitor = new Inquisitor(args[0]);
    }
    private final TimerBot timerBot;
    private final List<GuildBot> botList;
    private IDiscordClient client;
    public Inquisitor(String token){
        this.timerBot = new TimerBot();
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
            Execute.execute(true, true, event.getMessage().getAuthor().getID(), null, event.getMessage().getChannel().getID(), event.getMessage().getContent());
        }
    }
    @EventSubscriber
    public void handle(ReadyEvent event){
        Registry.start();
    }
    public TimerBot getTimerBot(){
        return this.timerBot;
    }
    public IDiscordClient getClient(){
        return this.client;
    }
    private void close(){
        this.botList.forEach(GuildBot::close);
    }
}
