package um.nija123098.inquisitor.commands;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.Presences;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.TimeHelper;

/**
 * Made by nija123098 on 11/13/2016
 */
public class Uptime {
    @Register(rank = Rank.NONE, startup = true, hidden = true)
    public static void monitor(){
        Inquisitor.discordClient().getDispatcher().registerListener(new Uptime());
        Inquisitor.discordClient().getUsers().forEach(iUser -> setPresence(User.getUserFromID(iUser.getID()), !iUser.getPresence().equals(Presences.OFFLINE)));
    }
    @Register(defaul = true, help = "Displays the time a user has been off or online")
    public static void uptime(Channel channel, String s){
        User user;
        if (s.length() == 0){
            user = User.getUserFromID(Inquisitor.discordClient().getOurUser().getID());
        }else{
            user = User.getUser(s);
        }
        if (user == null){
            MessageHelper.send(channel, "There is no user by that name");
            return;
        }
        s = user.getData("uptime");
        if (s == null){
            Log.error(user.discord().getName() + " does not have uptime data, setting now");
            setPresence(user, !user.discord().getPresence().equals(Presences.OFFLINE));
            s = user.getData("uptime");
        }
        String[] strings = s.split(":");
        MessageHelper.send(channel, user.discord().getName() + " has been " + (Boolean.parseBoolean(strings[0]) ? "on" : "off") + "line for **" + TimeHelper.format(TimeHelper.between(TimeHelper.currentTime(), strings[1])) + "**");
    }
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        setPresence(User.getUserFromID(event.getUser().getID()), !event.getNewPresence().equals(Presences.OFFLINE));
    }
    private static void setPresence(User user, boolean on){
        String s = user.getData("uptime");
        if (s != null){
            if (!s.split(":")[0].equals(on + "")){
                setUser(user, on);
            }
        }else{
            setUser(user, on);
        }
    }
    private static void setUser(User user, boolean on){
        user.putData("uptime", on + ":" + TimeHelper.currentTime());
    }
}
