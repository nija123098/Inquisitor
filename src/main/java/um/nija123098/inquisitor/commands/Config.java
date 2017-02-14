package um.nija123098.inquisitor.commands;

import org.apache.commons.lang3.tuple.Triple;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Command;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.LangHelper;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/8/2016
 */
@Register(guild = true, rank = Rank.GUILD_ADMIN)
public class Config {
    @Register(natural = true, guild = true, rank = Rank.USER, override = true, guaranteedSuccess = true, help = "Changes or displays the prefix for the server")
    public static void prefix(String[] s, Guild guild, Channel channel, User user, Rank rank, MessageAid aid){
        Entity prefixEntity = Inquisitor.getEntity("prefixes");
        if (s.length == 0){
            if (prefixEntity.getData(guild) == null){
                aid.withContent("No prefix has been set for this guild, to add one use configure prefix");
            }else{
                MessageHelper.send(channel, "The prefix on this server is \"" + prefixEntity.getData(guild) + "\"");
            }
        }else if (Rank.isSufficient(Rank.GUILD_ADMIN, rank)){
            prefixEntity.putData(guild, s[0]);
            MessageHelper.sendOverride(channel, "Prefix set to \"" + prefixEntity.getData(guild) + "\"");
        }else{
            MessageHelper.send(channel, user.discord().mention() + ", you do not have permission to set " + Inquisitor.ourUser().mention() + "'s prefix for " + guild.discord().getName());
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
    @Register(help = "Disables the following command")
    public static void blacklist(Channel channel, Guild guild, String s){
        Triple<Command, Boolean, String> triple = Registry.getCommand(s);
        if (triple.getLeft() == null){
            MessageHelper.sendOverride(channel, StringHelper.addQuotes(s) + " is not a recognized command");
        }else if (guild.getData("blacklist").contains(triple.getLeft().name())){
            MessageHelper.sendOverride(channel, "That command has already been blacklisted");
        }else if (triple.getLeft().name().contains("blacklist")){
            MessageHelper.sendOverride(channel, "You can not blacklist a blacklist command");
        }else{
            guild.putData("blacklist", guild.getData("blacklist", "") + ":" + triple.getLeft().name());
            MessageHelper.sendOverride(channel, "```md\n# Now blocking\n[" + triple.getLeft().name() + "](" + triple.getLeft().help() + ")\n# on server " + guild.discord().getName() + "```");
        }
    }
    @Register(help = "Enables the following command")
    public static void unblacklist(Channel channel, Guild guild, String s) {
        Triple<Command, Boolean, String> triple = Registry.getCommand(s);
        if (triple.getLeft() == null){
            MessageHelper.sendOverride(channel, StringHelper.addQuotes(s) + " is not a recognized command");
        }else if (!guild.getData("blacklist").contains(triple.getLeft().name())){
            MessageHelper.sendOverride(channel, "That command is not blacklisted");
        }else{
            guild.putData("blacklist", guild.getData("blacklist", "").replace(":" + triple.getLeft().name(), ""));
            MessageHelper.sendOverride(channel, "```md\n# No longer blocking\n[" + triple.getLeft().name() + "](" + triple.getLeft().help() + ")\n# on server " + guild.discord().getName() + "```");
        }
    }
    @Register(help = "Sets the user as the liaison for this bot for the guild")
    public static void liaison(User user, Guild guild){
        guild.putData("liaison", user.getID());
        MessageHelper.send(user, user.discord().getName() + ", you are now " + Inquisitor.ourUser().mention() + "'s liaison for the guild " + guild.discord().getName());
    }
    @Register(help = "Sets the language for this guild")
    public static void setLang(Guild guild, String s, MessageAid aid){
        if (LangHelper.setLang(guild, s)){
            aid.withContent("Your language has been set to " + s);
        }else{
            aid.withContent("\"" + s  + "\" is not a supported or valid language");
        }
    }
}
