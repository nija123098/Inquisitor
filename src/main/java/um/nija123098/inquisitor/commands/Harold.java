package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.AudioHelper;
import um.nija123098.inquisitor.util.LangHelper;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 2/4/2017.
 */
@Register(guild = true)
public class Harold {
    private static final Map<IVoiceChannel, String> VOICE_CHANNELS = new ConcurrentHashMap<>();
    @Register(defaul = true, help = "Makes the bot join the channel and announce things")
    public static Boolean harold(IVoiceChannel channel, User user, Guild guild, MessageAid aid){
        if (channel == null){
            aid.withContent("You are not in a voice channel in this guild");
            return false;
        }
        if (VOICE_CHANNELS.keySet().contains(channel)){
            aid.withContent("That channel is already being harolded, use harold disband to stop the announcements");
            return false;
        }
        for (IVoiceChannel chan : VOICE_CHANNELS.keySet()){
            if (chan.getGuild().equals(chan.getGuild()) && !chan.equals(channel)){
                aid.withContent("A channel in this guild is already being harolded");
                return false;
            }
        }
        VOICE_CHANNELS.put(channel, LangHelper.getLang(user, guild).getKey());
        channel.join();
        AudioHelper.say(VOICE_CHANNELS.get(channel), channel, new Pair<>("Inquisitor", false), new Pair<>(" has joined the channel", true));
        return true;
    }
    @Register(help = "Stops the heralding")
    public static Boolean disband(IVoiceChannel channel, MessageAid aid){
        if (channel == null){
            aid.withContent("You are not in a voice channel in this guild");
            return false;
        }
        String l = VOICE_CHANNELS.get(channel);
        if (l != null){
            AudioHelper.say(l, channel, new Pair<>("Inquisitor", false), new Pair<>(" is leaving the channel", true));
            RequestHandler.request(6000, channel::leave);
            return true;
        }else{
            aid.withContent("You are not in a channel with harolding active");
        }
        return false;
    }
    @Register(startup = true)
    public static void startup(){
        Inquisitor.registerListener((IListener<UserVoiceChannelJoinEvent>) join -> {
            move(true, join.getUser(), join.getVoiceChannel());
        });
        Inquisitor.unregisterListener((IListener<UserVoiceChannelLeaveEvent>) leave -> {
            if (VOICE_CHANNELS.keySet().contains(leave.getVoiceChannel())){
                if (leave.getVoiceChannel().getConnectedUsers().size() == 0){
                    VOICE_CHANNELS.remove(leave.getVoiceChannel());
                    leave.getVoiceChannel().leave();
                }else{
                    move(false, leave.getUser(), leave.getVoiceChannel());
                }
            }
        });
        Inquisitor.unregisterListener((IListener<UserVoiceChannelMoveEvent>) move -> {
            move(true, move.getUser(), move.getNewChannel());
            if (VOICE_CHANNELS.keySet().contains(move.getOldChannel())){
                if (move.getVoiceChannel().getConnectedUsers().size() == 0){
                    VOICE_CHANNELS.remove(move.getOldChannel());
                    move.getVoiceChannel().leave();
                }else{
                    move(false, move.getUser(), move.getOldChannel());
                }
            }
        });
    }
    private static void move(boolean join, IUser user, IVoiceChannel channel){
        String lang = VOICE_CHANNELS.get(channel);
        if (lang != null){
            AudioHelper.say(lang, channel, new Pair<>(user.getDisplayName(channel.getGuild()), false), new Pair<>(" has " + (join ? "joined" : "left") + " the channel", true));
        }
    }
    @Register(shutdown = true)
    public static void shutdown(){
        VOICE_CHANNELS.keySet().forEach(IVoiceChannel::leave);
    }
}
