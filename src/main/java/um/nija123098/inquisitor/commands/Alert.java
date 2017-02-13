package um.nija123098.inquisitor.commands;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Made by nija123098 on 12/10/2016
 */
public class Alert {
    private static final List<AlertWatch> WATCHES = new CopyOnWriteArrayList<>();
    private static final Map<String, List<String>> BOT_WATCHES = new ConcurrentHashMap<>();
    @EventSubscriber
    public void handle(PresenceUpdateEvent event){
        WATCHES.forEach(alertWatch -> {
            if (alertWatch.condition(event)){
                alertWatch.satisfied();
                WATCHES.remove(alertWatch);
            }
        });
        if (event.getUser().isBot() && event.getOldPresence().getStatus() != event.getNewPresence().getStatus() && BOT_WATCHES.containsKey(event.getUser().getID())){
            BOT_WATCHES.get(event.getUser().getID()).forEach(s -> MessageHelper.send(User.getUserFromID(s), event.getUser().mention() + " has gone " + event.getNewPresence().getStatus().name().toLowerCase()));
        }
    }
    @Register(startup = true, rank = Rank.NONE)
    public static void startup(Entity entity){
        for (String saved : entity.getSaved()) {
            String src = entity.getData(saved);
            String[] dat = src.split(":");
            String data = src.substring(dat[0].length() + 1);
            switch (dat[0]){
                case "TimeWatch":
                    WATCHES.add(new TimeWatch(data));
                    break;
                case "PresenceWatch":
                    WATCHES.add(new PresenceWatch(data));
                    break;
                case "GameWatch":
                    WATCHES.add(new GameWatch(data));
                    break;
                case "NoStatusWatch":
                    WATCHES.add(new NoStatusWatch(data));
                    break;
                default:
                    List<String> strings = new ArrayList<>();
                    Collections.addAll(strings, dat);
                    BOT_WATCHES.put(saved.split("-")[1], strings);
            }
        }
        Inquisitor.registerListener(new Alert());
    }
    @Register(shutdown = true, rank = Rank.NONE)
    public static void shutdown(Entity entity){
        entity.clearData();
        for (int i = 0; i < WATCHES.size(); i++) {
            entity.putData(i + "", WATCHES.get(i).getClass().getSimpleName() + ":" + WATCHES.get(i).save());
        }
        for (int i = 0; i < BOT_WATCHES.size(); i++) {
            BOT_WATCHES.forEach((s, strings) -> {
                final String[] str = {""};
                strings.forEach(st -> str[0] += st + ":");
                entity.putData("BotWatch-" + s, str[0]);
            });
        }
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
            WATCHES.add(new TimeWatch(timeVal, user, string));
            return true;
        }
        return false;
    }
    public static class TimeWatch extends AlertWatch{
        private final long millis;
        private final String userId;
        private final String todo;
        public TimeWatch(String s){
            String[] strings = s.split(":");
            this.millis = Long.parseLong(strings[0]);
            this.userId = strings[1];
            this.todo = s.substring((this.millis + "").length() + this.userId.length() + 2);
            this.send();
        }
        public TimeWatch(long target, User user, String todo){
            this.millis = target + System.currentTimeMillis();
            this.userId = user.getID();
            this.todo = todo;
            this.send();
        }
        private void send(){
            RequestHandler.schedule(this.millis, () -> MessageHelper.send(User.getUserFromID(this.userId), "You asked me to remind you to " + this.todo));
        }
        @Override
        protected boolean condition(PresenceUpdateEvent event){
            return this.millis < System.currentTimeMillis();
        }
        @Override
        protected void satisfied() {// never checked
            WATCHES.remove(this);
        }
        @Override
        protected String save() {
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
        WATCHES.add(new PresenceWatch(user, target, presence[0]));
        return true;
    }
    public static class PresenceWatch extends AlertWatch{
        private final User user;
        private final User target;
        private final StatusType presence;
        public PresenceWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUserFromID(strings[0]);
            this.target = User.getUserFromID(strings[1]);
            this.presence = StatusType.valueOf(strings[2]);
        }
        public PresenceWatch(User user, User target, StatusType presence) {
            this.user = user;
            this.target = target;
            this.presence = presence;
        }
        @Override
        protected boolean condition(PresenceUpdateEvent event) {
            return event.getUser().equals(this.target.discord()) && event.getNewPresence().getStatus() == this.presence;
        }
        @Override
        protected void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is now " + this.presence.name().toLowerCase());
        }
        @Override
        protected String save(){
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
            WATCHES.add(new GameWatch(statusName, user, target));
        }else{
            MessageHelper.send(channel, "No such user " + StringHelper.addQuotes(strings[0]));
            return false;
        }
        MessageHelper.send(channel, Inquisitor.ourUser().mention() + " will alert you when " + user.discord().getName() + " starts playing " + statusName);
        return true;
    }
    public static class GameWatch extends AlertWatch{
        private String status;
        private final User user;
        private final User target;
        private StatusType type;
        public GameWatch(String game, User user, User target) {
            this.status = game;
            this.user = user;
            this.target = target;
        }
        public GameWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUser(strings[0]);
            this.target = User.getUser(strings[1]);
            this.status = s.substring(strings[0].length() + strings[1].length() + 2);
        }
        @Override
        protected boolean condition(PresenceUpdateEvent event) {
            if (event.getUser().equals(this.target.discord()) && (event.getNewPresence().getStatus() == StatusType.ONLINE || event.getNewPresence().getStatus() == StatusType.STREAMING) && event.getNewPresence().getPlayingText().isPresent() && this.status.startsWith(event.getNewPresence().getPlayingText().get())){
                this.status = event.getNewPresence().getPlayingText().orElse("");
                this.type = event.getNewPresence().getStatus();
                return true;
            }
            return false;
        }
        @Override
        protected void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is now " + (this.type == StatusType.ONLINE ? "playing " : "streaming ") + this.status);
        }
        @Override
        protected String save() {
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
        WATCHES.add(new NoStatusWatch(user, target));
        return true;
    }
    public static class NoStatusWatch extends AlertWatch{
        private final User user;
        private final User target;
        public NoStatusWatch(User user, User target) {
            this.user = user;
            this.target = target;
        }
        public NoStatusWatch(String s){
            String[] strings = s.split(":");
            this.user = User.getUserFromID(strings[0]);
            this.target = User.getUserFromID(strings[1]);
        }
        @Override
        protected boolean condition(PresenceUpdateEvent event) {
            return event.getUser().equals(this.target.discord()) && event.getNewPresence().getStatus() == StatusType.ONLINE;
        }
        @Override
        protected void satisfied(){
            MessageHelper.send(this.user, this.target.discord().getName() + " is no longer playing a game");
        }
        @Override
        protected String save() {
            return this.user.getID() + this.target.getID();
        }
    }
    @Register(help = "Sets a status alert for a bot, to disable do @Inquisitor botwatch @MyBot false")
    public static boolean botWatch(User user, Guild guild, String string, MessageAid aid){
        String[] strings = string.split(" ");
        User bot = User.getUser(strings[0], guild);
        if (bot == null){
            aid.withRawContent(StringHelper.addQuotes(strings[0])).withContent(" is not a recognized user");
            return false;
        }
        if (!bot.discord().isBot()){
            aid.withRawContent(StringHelper.addQuotes(strings[0])).withContent(" is not a bot");
            return false;
        }
        BOT_WATCHES.putIfAbsent(bot.getID(), new ArrayList<>());
        boolean watch = true;
        if (strings.length > 1){
            try{watch = Boolean.parseBoolean(strings[1]);
            }catch(Exception e){
                aid.withRawContent(StringHelper.addQuotes(strings[1])).withContent(" is not true or false");
                return false;
            }
        }
        if (watch){
            if (BOT_WATCHES.get(bot.getID()).contains(user.getID())){
                aid.withContent("You are already watching ").withRawContent(bot.discord().getDisplayName(guild.discord()));
                return false;
            }else{
                BOT_WATCHES.get(bot.getID()).add(user.getID());
                aid.withContent("You are now watching ").withRawContent(bot.discord().getDisplayName(guild.discord()));
                return true;
            }
        }else{
            if (BOT_WATCHES.get(bot.getID()).contains(user.getID())){
                BOT_WATCHES.get(bot.getID()).remove(user.getID());
                aid.withContent("You are no longer watching ").withRawContent(bot.discord().getDisplayName(guild.discord()));
                return true;
            }else{
                aid.withContent("You are not watching ").withRawContent(bot.discord().getDisplayName(guild.discord()));
                return false;
            }
        }
    }
    private abstract static class AlertWatch{
        protected abstract void satisfied();
        protected boolean condition(PresenceUpdateEvent event){
            return false;
        }
        protected abstract String save();
    }
}
