package um.nija123098.inquisitor.commands;

import org.eclipse.jetty.util.ConcurrentHashSet;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.StatusType;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Made by nija123098 on 12/10/2016
 */
public class Alert {
    private static final Set<AlertWatch> WATCHES = new ConcurrentHashSet<>();
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        WATCHES.forEach(alertWatch -> {
            if (alertWatch.condition(event)){
                alertWatch.satisfied();
                alertWatch.remove();
            }
        });
    }
    @Register(startup = true, rank = Rank.NONE)
    public static void startup(Entity entity){
        for (String saved : entity.getSaved()) {
            String src = entity.getData(saved);
            String[] dat = src.split(":");
            String data = src.substring(dat[0].length() + 1);
            switch (dat[0]){
                case "TimeWatch":
                    new TimeWatch(data);
                    break;
                case "PresenceWatch":
                    new PresenceWatch(data);
                    break;
                case "GameWatch":
                    new GameWatch(data);
                    break;
                case "NoStatusWatch":
                    new NoStatusWatch(data);
                    break;
                case "BotWatch":
                    new BotWatch(data);
            }
        }
        Inquisitor.registerListener(new Alert());
    }
    @Register(shutdown = true, rank = Rank.NONE)
    public static void shutdown(Entity entity){
        entity.clearData();
        final AtomicInteger i = new AtomicInteger();
        WATCHES.forEach(watch -> entity.putData(i.incrementAndGet() + "", watch.getClass().getSimpleName() + ":" + watch.save()));
    }
    @Register(defaul = true, help = "Specify a time to be reminded of something, ex: alert 2w1d4h5m7s do some work")
    public static Boolean alert(Channel channel, User user, String string, String[] strings){
        Long timeVal = FormatHelper.toMillis(strings[0]);
        if (timeVal == null){
            MessageHelper.send(channel, strings[0] + " is not a time");
        }else if (timeVal == 0){
            MessageHelper.send(channel, "You should go do that now then.");
        }else if (timeVal < 0){
            MessageHelper.send(channel, "You already should have done that.");
        }else{
            string = string.substring(strings[0].length() + 1);
            MessageHelper.send(channel, "I will remind you to " + string.replace(".", "") + " in " + FormatHelper.format(timeVal));
            new TimeWatch(timeVal, user, string);
            return true;
        }
        return false;
    }
    static class TimeWatch extends AlertWatch{
        private final long millis;
        private final String userId;
        private final String todo;
        TimeWatch(String s){
            String[] strings = s.split(":");
            this.millis = Long.parseLong(strings[0]);
            this.userId = strings[1];
            this.todo = s.substring((this.millis + "").length() + this.userId.length() + 2);
            this.send();
        }
        TimeWatch(long target, User user, String todo){
            this.millis = target + System.currentTimeMillis();
            this.userId = user.getID();
            this.todo = todo;
            this.send();
        }
        void send(){
            RequestHandler.schedule(this.millis, () -> MessageHelper.send(User.getUserFromID(this.userId), "You asked me to remind you to " + this.todo));
        }
        @Override
        boolean condition(PresenceUpdateEvent event){
            return this.millis < System.currentTimeMillis();
        }
        @Override
        void satisfied() {// never checked
            WATCHES.remove(this);
        }
        @Override
        String save() {
            return this.millis + ":" + this.userId + ":" + this.todo;
        }
    }
    @Register(help = "Notifies if a specified user changes their presence, it defaults to online")
    public static Boolean presence(Channel channel, User user, String[] s){
        User target = User.getUser(s[0]);
        final StatusType[] presence = {StatusType.ONLINE};
        if (s.length > 1){
            try{presence[0] = StatusType.valueOf(s[1].toUpperCase());
            }catch(Exception e){
                MessageHelper.send(channel, "No such presence " + StringHelper.addQuotes(s[1]));
                return false;
            }
        }
        if (target != null){
            if (user.equals(target) && user.discord().getPresence().getStatus() == presence[0]){
                MessageHelper.send(channel, user.discord().getName() + " is already " + presence[0].name().toLowerCase());
                return false;
            }
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(s[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + target.discord().getName() + (presence[0] == StatusType.ONLINE ? " comes " : " goes ") + presence[0].name().toLowerCase());
        new PresenceWatch(user, target, presence[0]);
        return true;
    }
    static class PresenceWatch extends AlertWatch{
        private final User user;
        private final User target;
        private final StatusType presence;
        PresenceWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUserFromID(strings[0]);
            this.target = User.getUserFromID(strings[1]);
            this.presence = StatusType.valueOf(strings[2]);
        }
        PresenceWatch(User user, User target, StatusType presence) {
            this.user = user;
            this.target = target;
            this.presence = presence;
        }
        @Override
        boolean condition(PresenceUpdateEvent event) {
            return event.getUser().equals(this.target.discord()) && event.getNewPresence().getStatus() == this.presence;
        }
        @Override
        void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is now " + this.presence.name().toLowerCase());
        }
        @Override
        String save(){
            return this.user.getID() + ":" + this.target + ":" + this.presence.name();
        }
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
            if (user.discord().equals(target.discord()) && (user.discord().getPresence().getStatus() == StatusType.ONLINE || user.discord().getPresence().getStatus() == StatusType.STREAMING) && user.discord().getPresence().getPlayingText().isPresent() && statusName.startsWith(user.discord().getPresence().getPlayingText().get())){
                MessageHelper.send(channel, target.discord().getName() + " is already  " + (user.discord().getPresence().getStatus() == StatusType.ONLINE ? "playing " : "streaming ") + user.discord().getPresence().getPlayingText().orElse(""));
                return false;
            }
            new GameWatch(statusName, user, target);
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(strings[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + user.discord().getName() + " starts playing " + statusName);
        return true;
    }
    static class GameWatch extends AlertWatch{
        private String status;
        private final User user;
        private final User target;
        private StatusType type;
        GameWatch(String game, User user, User target) {
            this.status = game;
            this.user = user;
            this.target = target;
        }
        GameWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUser(strings[0]);
            this.target = User.getUser(strings[1]);
            this.status = s.substring(strings[0].length() + strings[1].length() + 2);
        }
        @Override
        boolean condition(PresenceUpdateEvent event) {
            if (event.getUser().equals(this.target.discord()) && (event.getNewPresence().getStatus() == StatusType.ONLINE || event.getNewPresence().getStatus() == StatusType.STREAMING) && event.getNewPresence().getPlayingText().isPresent() && this.status.startsWith(event.getNewPresence().getPlayingText().get())){
                this.status = event.getNewPresence().getPlayingText().orElse("");
                this.type = event.getNewPresence().getStatus();
                return true;
            }
            return false;
        }
        @Override
        void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is now " + (this.type == StatusType.ONLINE ? "playing " : "streaming ") + this.status);
        }
        @Override
        String save() {
            return this.user.getID() + ":" + this.target.getID() + ":" + this.status;
        }
    }
    @Register(help = "Notifies if an specified user stops playing games")
    public static Boolean noStatus(Channel channel, User user, String[] strings, String s){
        User target = User.getUser(strings[0]);
        if (target != null){
            if (user.equals(target) && user.discord().getPresence().getStatus() == StatusType.ONLINE){
                MessageHelper.send(channel, target.discord().getName() + " is not playing a game already");
                return false;
            }
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(strings[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + user.discord().getName() + " is no longer playing a game");
        new NoStatusWatch(user, target);
        return true;
    }
    static class NoStatusWatch extends AlertWatch{
        private final User user;
        private final User target;
        NoStatusWatch(User user, User target) {
            this.user = user;
            this.target = target;
        }
        NoStatusWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUserFromID(strings[0]);
            this.target = User.getUserFromID(strings[1]);
        }
        @Override
        boolean condition(PresenceUpdateEvent event) {
            return event.getUser().equals(this.target.discord()) && event.getNewPresence().getStatus() == StatusType.ONLINE;
        }
        @Override
        void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is no longer playing a game");
        }
        @Override
        String save() {
            return this.user.getID() + this.target.getID();
        }
    }
    @Register
    public static void botWatch(User user, String[] s, MessageAid aid) {
        User target = User.getUser(s[0]);
        if (target == null) {
            aid.withToggleContent(true, StringHelper.addQuotes(s[0]), " is not a known user");
            return;
        }
        if (!target.discord().isBot()) {
            aid.withToggleContent(true, target.discord().mention(), ", is not a bot");
            return;
        }
        Boolean startWatch;
        startWatch = s.length <= 1 || Boolean.parseBoolean(s[1]);
        if (WATCHES.stream().filter(watch -> watch instanceof BotWatch && ((BotWatch) watch).watcher.equals(user) && ((BotWatch) watch).target.equals(target)).count() > 0) {
            if (startWatch) {
                aid.withToggleContent(false, "You are already watching ", target.discord().mention());
            } else {
                WATCHES.removeIf(watch -> watch instanceof BotWatch && ((BotWatch) watch).watcher.equals(user) && ((BotWatch) watch).target.equals(target));
                aid.withToggleContent(false, "You are no longer watching ", target.discord().mention());
            }
        } else {
            if (startWatch) {
                aid.withToggleContent(false, "You are now watching ", target.discord().mention());
                new BotWatch(user, target);
            } else {
                aid.withToggleContent(false, "You are not watching ", target.discord().mention());
            }
        }
    }
    static class BotWatch extends AlertWatch {
        User watcher, target;
        BotWatch(String s) {
            String[] strings = s.split("-");
            this.watcher = User.getUserFromID(strings[0]);
            this.target = User.getUserFromID(strings[1]);
        }
        BotWatch(User watcher, User target) {
            this.watcher = watcher;
            this.target = target;
        }
        @Override
        void satisfied() {}
        @Override
        boolean condition(PresenceUpdateEvent event){
            if (event.getNewPresence().getStatus() != event.getOldPresence().getStatus()){
                MessageHelper.send(User.getUserFromID(event.getUser().getID()), event.getUser().mention() + " is now " + event.getNewPresence().getStatus());
            }// no point in returning true to indicate to call two methods that do nothing
            return false;
        }
        @Override
        void remove() {}
        @Override
        String save() {
            return this.watcher.getID() + "-" + this.target.getID();
        }
    }
    private static abstract class AlertWatch{
        AlertWatch(){
            WATCHES.add(this);
        }
        abstract void satisfied();
        boolean condition(PresenceUpdateEvent event){
            return false;
        }
        abstract String save();
        void remove(){
            WATCHES.remove(this);
        }
    }
}
