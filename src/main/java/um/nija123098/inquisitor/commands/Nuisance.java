package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageAid;
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
        Inquisitor.registerListener(new Monitor());
    }
    @Register(defaul = true)
    public static void nuisance(MessageAid aid){
        aid.withContent("These commands help in catching trolls.");
    }
    @Register(guild = true, rank = Rank.GUILD_ADMIN, help = "Sets the time threshold to alert the guild liaison that a user is spamming in millis or 0 to disable")
    public static boolean ratelimit(Guild guild, String s, MessageAid aid){
        s = s.replace("ms", "");
        try{Integer.parseInt(s);
        }catch(Exception e){
            aid.withToggleContent(false, StringHelper.addQuotes(s), " is not a number");
            return false;
        }
        guild.putData("ratelimit", s);
        if (s.equals("0")){
            aid.withContent("Rate limiting is now disabled for this guild");
        }else{
            aid.withToggleContent(false, "This guild now has a ", guild.getData("ratelimit") + "ms", " message rate limit");
        }
        return true;
    }
    private static final Map<Pair<String, String>, Long> MAP = new ConcurrentHashMap<>();
    public static class Monitor {
        @EventSubscriber
        public void handle(MessageReceivedEvent event){
            IGuild iGuild = event.getMessage().getGuild();
            if (!(event.getMessage().getAuthor().isBot() || event.getMessage().getChannel().isPrivate() || Rank.isSufficient(Rank.GUILD_ADMIN, Rank.getRank(User.getUserFromID(event.getMessage().getAuthor().getID()), iGuild == null ? null : Guild.getGuild(iGuild.getID()))))){
                Pair<String, String> pair = new Pair<>(event.getMessage().getAuthor().getID(), event.getMessage().getGuild().getID());
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
}
