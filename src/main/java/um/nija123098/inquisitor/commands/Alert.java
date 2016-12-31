package um.nija123098.inquisitor.commands;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.RequestHandler;
import um.nija123098.inquisitor.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 12/10/2016
 */
public class Alert {
    private static final List<AlertWatch> WATCHES = new ArrayList<AlertWatch>();
    @EventSubscriber
    public void handle(StatusChangeEvent event){
        List<AlertWatch> removes = new ArrayList<AlertWatch>();
        synchronized (WATCHES){
            WATCHES.forEach(alertWatch -> {
                if (alertWatch.condition(event)){
                    alertWatch.satisfied();
                    removes.add(alertWatch);
                }
            });
            WATCHES.removeAll(removes);
        }
    }
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        List<AlertWatch> removes = new ArrayList<AlertWatch>();
        synchronized (WATCHES){
            WATCHES.forEach(alertWatch -> {
                if (alertWatch.condition(event)){
                    alertWatch.satisfied();
                    removes.add(alertWatch);
                }
            });
            WATCHES.removeAll(removes);
        }
    }
    private static final Alert ALERT = new Alert();
    @Register(startup = true, rank = Rank.NONE)
    public static void startup(){
        Inquisitor.discordClient().getDispatcher().registerListener(ALERT);
    }
    @Register(shutdown = true, rank = Rank.NONE)
    public static void shutdown(){
        Inquisitor.discordClient().getDispatcher().unregisterListener(ALERT);
    }
    @Register(defaul = true, help = "Specify a time in hours to be reminded of something")
    public static Boolean alert(Channel channel, User user, String string, String[] strings){
        float hours;
        try{hours = Float.parseFloat(strings[0]);
        }catch(Exception e){
            MessageHelper.send(channel, strings[0] + " is not a time");
            return false;
        }
        if (hours == 0){
            MessageHelper.send(channel, "You should go do that now then.");
            return false;
        }else if (hours < 0){
            MessageHelper.send(channel, "You already should have done that.");
            return false;
        }
        string = string.substring(string.length() - strings[0].length());
        MessageHelper.send(channel, "I will remind you to " + string + " in " + hours + " hour" + (hours == 1 ? "" : "s"));
        final String finalS = string;
        RequestHandler.request((long) (hours * 60 * 60 * 1000), () -> MessageHelper.send(user, "You asked me to remind you to " + finalS));
        return true;
    }
    @Register(help = "Notifies if a specified user changes their presence, it defaults to online")
    public static Boolean presence(Channel channel, User user, String[] s){
        User target = User.getUser(s[0]);
        final Presences[] presence = {Presences.ONLINE};
        if (s.length > 1){
            try{presence[0] = Presences.get(s[1].toUpperCase());
            }catch(Exception e){
                MessageHelper.send(channel, "No such presence " + StringHelper.addQuotes(s[1]));
                return false;
            }
        }
        if (target != null){
            if (user.equals(target) && user.discord().getPresence() == presence[0]){
                MessageHelper.send(channel, user.discord().getName() + " is already " + presence[0].name().toLowerCase());
                return false;
            }
            synchronized (WATCHES){
                WATCHES.add(new AlertWatch(){
                    @Override
                    protected boolean condition(PresenceUpdateEvent event) {
                        return event.getUser().equals(target.discord()) && event.getNewPresence() == presence[0];
                    }
                    @Override
                    protected void satisfied(){
                        MessageHelper.send(user, target.discord().getName() + " is now " + presence[0].name().toLowerCase());
                    }
                });
            }
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(s[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + target.discord().getName() + (presence[0] == Presences.ONLINE ? " comes " : " goes ") + presence[0].name().toLowerCase());
        return true;
    }
    @Register(help = "Notifies if a specified user changes their status to the one specified")
    public static Boolean gameStatus(Channel channel, User user, String[] strings, String s){
        if (s.length() == 0){
            MessageHelper.send(channel, "Games require names, please specify one!");
            return false;
        }
        User target = User.getUser(strings[0]);
        String statusName = s.substring((strings[0] + " ").length());
        if (target != null){
            if (user.discord().equals(target.discord()) && (user.discord().getStatus().getType() == Status.StatusType.GAME || user.discord().getStatus().getType() == Status.StatusType.STREAM) && statusName.startsWith(user.discord().getStatus().getStatusMessage())){
                MessageHelper.send(channel, target.discord().getName() + " is already  " + (user.discord().getStatus().getType() == Status.StatusType.GAME ? "playing " : "streaming ") + user.discord().getStatus().getStatusMessage());
                return false;
            }
            synchronized (WATCHES){
                WATCHES.add(new AlertWatch(){
                    private String status;
                    private Status.StatusType type;
                    @Override
                    protected boolean condition(StatusChangeEvent event) {
                        if (event.getUser().equals(target.discord()) && (event.getNewStatus().getType() == Status.StatusType.GAME || event.getNewStatus().getType() == Status.StatusType.STREAM) && statusName.startsWith(event.getNewStatus().getStatusMessage())){
                            this.status = event.getNewStatus().getStatusMessage();
                            this.type = event.getNewStatus().getType();
                            return true;
                        }
                        return false;
                    }
                    @Override
                    protected void satisfied(){
                        MessageHelper.send(user, target.discord().getName() + " is now " + (this.type == Status.StatusType.GAME ? "playing " : "streaming ") + this.status);
                    }
                });
            }
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(strings[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + user.discord().getName() + " starts playing " + statusName);
        return true;
    }
    @Register(help = "Notifies if an specified user stops playing games")
    public static Boolean noStatus(Channel channel, User user, String[] strings, String s){
        User target = User.getUser(strings[0]);
        if (target != null){
            if (user.equals(target) && user.discord().getStatus().getType() == Status.StatusType.NONE){
                MessageHelper.send(channel, target.discord().getName() + " is not playing a game already");
                return false;
            }
            synchronized (WATCHES){
                WATCHES.add(new AlertWatch(){
                    @Override
                    protected boolean condition(StatusChangeEvent event) {
                        return event.getUser().equals(target.discord()) && event.getNewStatus().getType() == Status.StatusType.NONE;
                    }
                    @Override
                    protected void satisfied(){
                        MessageHelper.send(user, target.discord().getName() + " is no longer playing a game");
                    }
                });
            }
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(strings[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + user.discord().getName() + " is no longer playing a game");
        return true;
    }
    private abstract static class AlertWatch{
        protected abstract void satisfied();
        protected boolean condition(PresenceUpdateEvent event){
            return false;
        }
        protected boolean condition(StatusChangeEvent event){
            return false;
        }
    }
}
