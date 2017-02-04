package um.nija123098.inquisitor.commands;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.Rand;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Made by nija123098 on 2/1/2017.
 */
@Register(guild = true)
public class Interact {
    private static final String[] EMOTICONS = {"arrow_up", "arrow_left", "arrow_down", "arrow_right"};
    private static final List<Response<? extends Event>> RESPONSES = new CopyOnWriteArrayList<>();
    @Register(rank = Rank.NONE, startup = true)
    public static void startup(){
        Inquisitor.discordClient().getDispatcher().registerListener(new ResponseListener());
    }
    @Register(name = "interact 2048", absoluteAliases = "play 2048")
    public static Boolean twentyFortyEight(Channel channel, MessageAid aid){
        if (!require(Bot.EMILY, channel)){
            return false;
        }
        aid.withoutNoSpace().withoutTranslateContent("<@" + Bot.EMILY.id + "> 2048");
        RESPONSES.add(new TwentyFortyEightInitResponse(channel));
        return true;
    }
    private static void twentyFortyEight(IMessage message, AtomicBoolean run){
        RequestHandler.request(3000, () -> {
            if (run.get()){
                MessageHelper.react(EMOTICONS[Rand.integer(3)], message);
                twentyFortyEight(message, run);
            }
        });
    }
    private static class TwentyFortyEightInitResponse extends Response<MessageReceivedEvent> {
        TwentyFortyEightInitResponse(Channel channel) {
            super(null, null, MessageReceivedEvent.class);
            check = event -> {
                String content = event.getMessage().getContent();
                if (event.getAuthor().getID().equals(Bot.EMILY.id) && event.getChannel().getID().equals(channel.getID())) {
                    if (content.contains("ry again") && content.contains("from now")) {
                        RESPONSES.remove(this);
                    }
                    return event.getMessage().getContent().contains(Inquisitor.ourUser().getID());
                }
                return false;
            };
            final AtomicReference<TwentyFortyEightEndResponse> response = new AtomicReference<>();
            this.execute = event -> {
                response.set(new TwentyFortyEightEndResponse(event.getMessage()));
                twentyFortyEight(event.getMessage(), response.get().run);
            };
        }
    }
    private static class TwentyFortyEightEndResponse extends Response<ReactionRemoveEvent>{
        final AtomicBoolean run = new AtomicBoolean(true);
        TwentyFortyEightEndResponse(IMessage message) {
            super(event -> event.getMessage().getID().equals(message.getID()) && event.getAuthor().getID().equals(Bot.EMILY.id), null, ReactionRemoveEvent.class);
            this.execute = event -> run.set(false);
        }
    }
    private static boolean require(Bot bot, Channel channel){
        User botUser = User.getUserFromID(bot.id);
        if (!channel.discord().getGuild().getUsers().contains(botUser.discord())){
            return false;
        }
        EnumSet<Permissions> perms = channel.discord().getModifiedPermissions(botUser.discord());
        return perms.contains(Permissions.SEND_MESSAGES) && perms.contains(Permissions.READ_MESSAGES);
    }
    private enum Bot {
        EMILY("212834061306036224");
        Bot(String id){
            this.id = id;
        }
        String id;
    }
    private static class Response<E> {
        Predicate<E> check;
        Consumer<E> execute;
        Class<E> clazz;
        Response(Predicate<E> check, Consumer<E> execute, Class<E> clazz) {
            this.check = check;
            this.execute = execute;
            this.clazz = clazz;
        }
        public boolean attempt(Object o){
            if (clazz.isInstance(o) && check.test((E) o)){
                this.execute.accept(((E) o));
                return true;
            }
            return false;
        }
    }
    public static class ResponseListener implements IListener<Event>{
        @Override
        public void handle(Event event) {
            RESPONSES.forEach(response -> {
                if (response.attempt(event)){
                    RESPONSES.remove(response);
                }
            });
        }
    }
}
