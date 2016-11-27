package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/8/2016
 */
@Register(guild = true, rank = Rank.GUILD_ADMIN)
public class Config {
    @Register(natural = true, guild = true, rank = Rank.USER, override = true, help = "Changes or displays the prefix for the server")
    public static void prefix(String[] s, Guild guild, Channel channel, User user, Rank rank){
        if (s.length == 0){
            if (guild.getData("prefix") == null){
                MessageHelper.send(channel, "No prefix has been set for this guild, to add one use configure prefix");
            }else{
                MessageHelper.send(channel, "The prefix on this server is \"" + guild.getData("prefix") + "\"");
            }
        }else if (Rank.isSufficient(Rank.GUILD_ADMIN, rank)){
            guild.putData("prefix", s[0]);
            MessageHelper.sendOverride(channel, "Prefix set to \"" + guild.getData("prefix") + "\"");
        }else{
            MessageHelper.send(channel, user.discord().mention() + ", you do not have permission to set " + Inquisitor.discordClient().getOurUser().mention() + "'s prefix for " + guild.discord().getName());
        }
    }
    @Register(help = "Edits the bot's ability to speak in the channel")
    public static void chat(String s, Channel channel){
        if (StringHelper.affirmative(s.split(" ")[0])){
            channel.putData("chat_approved", "true");
            MessageHelper.sendOverride(channel, "Now allowed to chat in " + channel.discord().mention());
        }else if (StringHelper.negative(s.split(" ")[0])){
            channel.putData("chat_approved", "false");
            MessageHelper.sendOverride(channel, "No longer allowed to chat in " + channel.discord().mention());
        }else{
            MessageHelper.sendOverride(channel, "I did not understand if you want me to chat or not, please use yes or no");
        }
    }
    @Register(help = "Sets the user as the liaison for this bot for the guild")
    public static void liaison(User user, Guild guild){
        guild.putData("liaison", user.getID());
        MessageHelper.send(user, user.discord().mention() + ", you are now " + Inquisitor.discordClient().getOurUser().mention() + "'s liaison for the guild " + guild.discord().getName());
    }
}
