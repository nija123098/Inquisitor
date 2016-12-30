package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 12/30/2016
 */
public class Nuisance {
    @Register(startup = true)
    public static void startup(){
        Inquisitor.discordClient().getDispatcher().registerListener(new Monitor());
    }
    @Register(guild = true, rank = Rank.GUILD_ADMIN)
    public static void ratelimit(Guild guild, Channel channel, String s){
        s = s.replace("ms", "");
        try{Integer.parseInt(s);
        }catch(Exception e){
            MessageHelper.send(channel, StringHelper.addQuotes(s) + " is not a number");
        }
        guild.putData("ratelimit", s);
        MessageHelper.send(channel, "This guild now has a " + guild.getData("ratelimit") + "ms message rate limit");
    }
    private static final Map<Pair<String, String>, Long> MAP = new ConcurrentHashMap<Pair<String, String>, Long>();
    public static class Monitor {
        @EventSubscriber
        public void handle(MessageReceivedEvent event){
            if (!event.getMessage().getAuthor().isBot()){
                Pair<String, String> pair = new Pair<String, String>(event.getMessage().getAuthor().getID(), event.getMessage().getGuild().getID());
                long current = System.currentTimeMillis();
                if (MAP.get(pair) == null){
                    MAP.put(pair, current);
                    return;
                }
                long delta = current - MAP.get(pair);
                MAP.put(pair, current);
                Guild guild = Guild.getGuild(event.getMessage().getGuild().getID());
                if (guild != null && !guild.getData("ratelimit", "0").equals("0") && delta < Integer.parseInt(guild.getData("ratelimit"))){
                    MessageHelper.send(Guild.getGuild(event.getMessage().getGuild().getID()), "User " + event.getMessage().getAuthor().getName() + "#" + event.getMessage().getAuthor().getDiscriminator() + " exceeded the rate limit at " + Integer.parseInt(guild.getData("ratelimit")) + "ms");
                }
            }
        }
    }
    public static void main(String[] args) {
        System.out.println(new Pair<String, String>("ONE", "TWO").equals(new Pair<String, String>("ONE", "TWO")));
    }
}
