package um.nija123098.inquisitor.commands;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.StatusType;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/12/2016
 */
@Register(name = "suspicion", suspicion = Suspicion.HERETICAL)
public class Susp {
    @Register(startup = true, rank = Rank.NONE)
    public static void setUp(){
        Inquisitor.registerListener(new Susp());
    }
    @EventSubscriber
    public void hande(MessageReceivedEvent event){
        if (event.getMessage().getAuthor().getPresence().getStatus() == StatusType.OFFLINE){
            Suspicion.addLevel(User.getUserFromID(event.getMessage().getAuthor().getID()), .3f, null, false);
        }
    }
    @EventSubscriber
    public void handle(NicknameChangedEvent event){
        Suspicion.addLevel(User.getUserFromID(event.getUser().getID()), .25f, null, false);
    }
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        User user = User.getUserFromID(event.getUser().getID());
        if (event.getNewPresence().getStatus().equals(StatusType.DND)){
            Suspicion.addLevel(user, .3f, null, false);
        }else if (event.getNewPresence().getStatus().equals(StatusType.ONLINE)){
            if (!Suspicion.isSufficient(Suspicion.LOYAL, Suspicion.getLevel(user))){
                Suspicion.addLevel(user, -.25f, null, false);
            }
        }
    }
    @Register(defaul = true, suspicious = .1f, help = "Displays the suspicion level of the user")
    public static Boolean suspicion(User user, Suspicion suspicion, Guild guild, String s, MessageAid aid){
        if (s.length() == 0){
            aid.withToggleContent(true, user.discord().getDisplayName(guild.discord()), ", you are " + suspicion.name());
        }else{
            User u = User.getUser(s, guild);
            if (u != null){
                aid.withToggleContent(true, u.discord().getName() + "#" + u.discord().getDiscriminator(), " has been ", Suspicion.getLevel(u).name() + " (" + Suspicion.getValue(u) + ")");
            }else{
                aid.withContent("No user has been found by that name");
                return false;
            }
        }
        return true;
    }
    @Register(rank = Rank.BOT_ADMIN, help = "Sets a user's suspicion level")
    public static Boolean set(User user, String s, MessageAid aid){
        float v;
        try{v = Float.parseFloat(s);
        }catch(Exception e){
            aid.withToggleContent(true, StringHelper.addQuotes(s), " is not a number");
            return false;
        }
        Suspicion.setLevel(user, v, true);
        return suspicion(user, Suspicion.getLevel(user), null, "", aid);
    }
}
