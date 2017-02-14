package um.nija123098.inquisitor.commands;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.StatusType;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.FormatHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageAid;

/**
 * Made by nija123098 on 11/13/2016
 */
public class Uptime {
    private static Entity entity;
    @Register(rank = Rank.NONE, startup = true, hidden = true)
    public static void monitor(Entity entity){
        Uptime.entity = entity;
        Inquisitor.registerListener(new Uptime());
        Inquisitor.discordClient().getUsers().forEach(user -> setPresence(User.getUserFromID(user.getID()), !user.getPresence().getStatus().equals(StatusType.OFFLINE)));
    }
    @Register(defaul = true, help = "Displays the time a user has been off or online")
    public static Boolean uptime(String s, Entity entity, MessageAid aid, Guild guild){
        User user;
        if (s.length() == 0){
            user = User.getUserFromID(Inquisitor.ourUser().getID());
        }else{
            user = User.getUser(s);
            if (user == null){
                aid.withContent("There is no user by that name");
                return false;
            }
        }
        s = entity.getData(user);
        if (s == null){
            Log.error(user.discord().getName() + " does not have uptime data, setting now");
            setPresence(user, !user.discord().getPresence().getStatus().equals(StatusType.OFFLINE));
            s = entity.getData(user);
        }
        String[] strings = s.split(":");
        aid.withToggleContent(true, user.discord().getDisplayName(guild.discord()), " has been " + (Boolean.parseBoolean(strings[0]) ? "on" : "off") + "line for", " **" + FormatHelper.format(System.currentTimeMillis() - Long.parseLong(strings[1])) + "**");
        return true;
    }
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        setPresence(User.getUserFromID(event.getUser().getID()), !event.getNewPresence().getStatus().equals(StatusType.OFFLINE));
    }
    private static void setPresence(User user, boolean on){
        String s = entity.getData(user);
        if (s != null && s.split(":")[0].equals(on + "")) {
            return;
        }
        entity.putData(user, on + ":" + System.currentTimeMillis());
    }
}
