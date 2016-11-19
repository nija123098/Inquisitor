package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Rank;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 11/8/2016
 */
public class Config {
    @Register(natural = true, guild = true, help = "Changes or displays the prefix for the server")
    public static void prefix(String[] s, Guild guild, Channel channel, Rank rank){
        if (s.length == 0){
            MessageHelper.send(channel, "The prefix on this server is \"" + guild.getData("prefix") + "\"");
        }else if (rank.ordinal() >= Rank.GUILD_ADMIN.ordinal()){
            guild.putData("prefix", s[0]);
            MessageHelper.sendOverride(channel, "Prefix set to \"" + guild.getData("prefix") + "\"");
        }else{
            MessageHelper.send(channel, "Only Guild Admins and above can set the prefix for this guild\nMentioning me works as a prefix");
        }
    }
    @Register(rank = Rank.GUILD_ADMIN, guild = true, help = "Edits the bot's ability to speak in the channel")
    public static void chat(String s, Channel channel){
        switch (s.split(" ")[0]){
            case "1":
            case "true":
            case "yes":
            case "affirmative":
                channel.putData("chat_approved", "true");
                MessageHelper.sendOverride(channel, "Now allowed to chat in " + channel.discord().mention());
                break;
            case "0":
            case "false":
            case "no":
            case "negative":
                channel.putData("chat_approved", "false");
                MessageHelper.sendOverride(channel, "No longer allowed to chat in " + channel.discord().mention());
                break;
            default:
                MessageHelper.sendOverride(channel, "I did not understand if you want me to chat or not, please use yes or no");
        }
    }
    @Register(guild = true, rank = Rank.GUILD_ADMIN, help = "Sets the user as the liaison for this bot for the guild")
    public static void liaison(User user, Guild guild){
        guild.putData("liaison", user.getID());
        MessageHelper.send(user, user.discord().mention() + ", you are now " + Inquisitor.discordClient().getOurUser().mention() + "'s liaison for the guild " + guild.discord().getName());
    }
}