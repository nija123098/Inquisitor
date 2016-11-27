package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageList;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.*;
import um.nija123098.inquisitor.context.*;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/7/2016
 */
@Register(natural = true, suspicion = Suspicion.HERETICAL)
public class Basic {
    @Register(hidden = true)
    public static void ping(Channel channel){
        MessageHelper.send(channel, "pong");
        Log.info("pong");
    }
    @Register(help = "Lists information on Inquisitor")
    public static void info(Channel channel){
        MessageHelper.send(channel, "" +
                "This is " + Inquisitor.discordClient().getOurUser().mention(false) + ", a Discord info bot.\n" +
                "It collects and displays information on servers, users, and bots for a benevolent purpose.\n" +
                Inquisitor.discordClient().getOurUser().mention() + " is made by nija123098#7242");
    }
    @Register(guild = true, help = "Displays the guild's prefix for this bot, to configure use config prefix")
    public static void prefix(Guild guild, Channel channel){
        if (guild.getData("prefix") == null){
            MessageHelper.send(channel, "No prefix has been set for this guild, to add one use configure prefix");
        }else{
            MessageHelper.send(channel, "The prefix for this guild is \"" + guild.getData("prefix") + "\"");
        }
    }
    @Register(help = "Displays all commands or help on a specific command")
    public static void help(Channel channel, User user, Rank rank, String s){
        if (s.equals("")){
            MessageHelper.checkYourDMs(channel, user);
            final Rank finalRank = rank;
            CommonMessageHelper.displayCommands("# Help at rank " + rank.name().replace("_", " "), "", Registry.getCommands(command -> command.rankSufficient(finalRank), Command::surface, command -> !command.hidden()), user);
        }else{
            Command command = Registry.getCommand(s);
            if (command != null){
                MessageHelper.send(channel, command.help());
            }else{
                MessageHelper.send(channel, "No such command");
            }
        }
    }
    @Register(hidden = true, help = "Displays all commands of a specific rank")
    public static void helpatrank(Channel channel, User user, Rank rank, String s){
        Rank targetRank = null;
        try{targetRank = Rank.valueOf(s.toUpperCase().replace(" ", "_"));
        }catch(Exception ignored){}
        if (targetRank == null){
            MessageHelper.send(channel, "No such rank as \"" + s + "\"");
        }else if (Rank.isSufficient(targetRank, rank)){
            help(channel, user, targetRank, "");
        }else{
            MessageHelper.send(user, "You can not check commands for ranks you are not above or at");
        }
    }
    @Register(help = "Lists commands for a default command")
    public static void commands(Channel channel, User user, String s){
        Command command = Registry.getCommand(s);
        if (command == null || command.hidden()){
            MessageHelper.send(channel, "No such command");
        }else if (!command.defaul()){
            MessageHelper.send(channel, "That command is not a default command");
        }else{
            MessageHelper.checkYourDMs(channel, user);
            CommonMessageHelper.displayCommands("# All extension commands for " + command.name().split(" ")[0] + "\n[" + command.name() + "](" + command.help() + ")", "", Registry.getCommands(c -> c.name().split(" ")[0].equals(command.name().split(" ")[0]), c -> !c.defaul()), user);
        }
    }
    @Register(suspicion = Suspicion.RADICAL, help = "Displays the invite link for Inquisitor")
    public static void invite(Channel channel){
        MessageHelper.send(channel, "You must have the manage server permission to add this bot to the server\n" +
                "https://discordapp.com/oauth2/authorize?client_id=244634255727132673&scope=bot");
    }
    @Register(help = "Displays the GitHub link to Inquisitor's repo")
    public static void gitHub(Channel channel){
        MessageHelper.send(channel, "https://github.com/nija123098/Inquisitor");
    }
    @Register(rank = Rank.GUILD_ADMIN, help = "Deletes the bot's previous messages, message count specifiable")
    public static void takeback(Channel channel, String[] s){
        int count;
        if (s.length == 0){
            count = 1;
        }else{
            try{count = Integer.parseInt(s[0]);
            }catch(Exception e){
                MessageHelper.send(channel, s[0] + " is not a number");
                return;
            }
            if (count > 100){
                count = 100;
            }
        }
        MessageList messages = channel.discord().getMessages();
        IUser user = Inquisitor.discordClient().getOurUser();
        List<IMessage> deletes = new ArrayList<IMessage>(count);
        int i = 0;
        while (count != deletes.size()){
            if (messages.get(i).getAuthor().equals(user)){
                deletes.add(messages.get(i));
            }
            ++i;
        }
        if (deletes.size() > 1){
            deletes.forEach(iMessage -> RequestHandler.request(iMessage::delete));
            /*if (channel.isPrivate()){
            }else{
                RequestHandler.request(() -> messages.bulkDelete(deletes));
            }*/// Bulk delete not currently working
        }else{
            RequestHandler.request(() -> deletes.get(0).delete());
        }
        MessageHelper.send(channel, "Deleted " + deletes.size() + " message" + (deletes.size() > 1 ? "s" : ""), 3000);
    }
    @Register(rank = Rank.BOT_ADMIN, help = "Inquisitor repeats the text")
    public static void say(Channel channel, String s){
        if (s.length() != 0){
            MessageHelper.send(channel, s);
        }
    }
}
