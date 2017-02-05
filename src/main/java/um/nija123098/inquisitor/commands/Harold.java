package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
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
            String lang = VOICE_CHANNELS.get(join.getVoiceChannel());
            if (lang != null){
                AudioHelper.say(lang, join.getVoiceChannel(), new Pair<>(join.getUser().getDisplayName(join.getGuild()), false), new Pair<>(" has joined the channel", true));
            }
        });
        Inquisitor.unregisterListener((IListener<UserVoiceChannelLeaveEvent>) leave -> {
            String lang = VOICE_CHANNELS.get(leave.getVoiceChannel());
            if (lang != null){
                if (leave.getVoiceChannel().getConnectedUsers().size() == 0){
                    VOICE_CHANNELS.remove(leave.getVoiceChannel());
                    leave.getVoiceChannel().leave();
                }else{
                    AudioHelper.say(lang, leave.getVoiceChannel(), new Pair<>(leave.getUser().getDisplayName(leave.getGuild()), false), new Pair<>(" has left the channel", true));
                }
            }
        });
    }
    @Register(shutdown = true)
    public static void shutdown(){
        VOICE_CHANNELS.keySet().forEach(IVoiceChannel::leave);
    }
}
