package um.nija123098.inquisitor.commands;

import org.apache.commons.lang3.tuple.Triple;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageList;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Command;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/7/2016
 */
@Register(natural = true, suspicion = Suspicion.HERETICAL)
public class Basic {
    @Register(hidden = true, aliases = "pong")
    public static void ping(Channel channel, IMessage message){
        MessageHelper.react("ping_pong", message);
        Log.info("pong");
    }
    @Register(help = "Lists information on Inquisitor")
    public static void info(Channel channel, MessageAid aid){
        aid.withContent("This is ").withRawContent(Inquisitor.mention()).withContent(", a Discord info bot.\n" +
                "It collects and displays information on servers, users, and bots for a benevolent purpose.\n" +
                "To set a prefix other than mentioning me do `@Inquisitor prefix ?` to set the prefix to `?`" +
                Inquisitor.ourUser().mention() + " is made by nija123098#7242");
    }
    @Register(help = "Displays all commands or help on a specific command")
    public static void help(Channel channel, User user, Rank rank, String s){
        if (s.equals("")){
            MessageHelper.checkYourDMs(channel, user);
            final Rank finalRank = rank;
            CommonMessageHelper.displayCommands("# Help at rank " + rank.name().replace("_", " "), "", Registry.getCommands(command -> command.rankSufficient(finalRank), Command::surface, command -> !command.hidden()), user);
        }else{
            Triple<Command, Boolean, String> triple = Registry.getCommand(s);
            if (triple.getLeft() != null){
                MessageHelper.send(channel, triple.getLeft().help());
            }else{
                MessageHelper.send(channel, "No such command");
            }
        }
    }
    @Register(hidden = true, help = "Displays all commands of a specific rank")
    public static void helpAtRank(Channel channel, User user, Rank rank, String s){
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
    public static void commands(Channel channel, User user, Rank rank, String s){
        if (s.length() == 0){
            help(channel, user, rank, s);
        }else{
            Triple<Command, Boolean, String> triple = Registry.getCommand(s);
            if (triple.getLeft() == null) {
                MessageHelper.send(channel, "No such command " + StringHelper.addQuotes(s));
            }else if (triple.getLeft().hidden()){
                MessageHelper.send(channel, "We don't talk about that command");
            }else if (!triple.getLeft().defaul()){
                MessageHelper.send(channel, "That command is not a default command");
            }else{
                MessageHelper.checkYourDMs(channel, user);
                CommonMessageHelper.displayCommands("# All extension commands for " + triple.getLeft().name().split(" ")[0] + "\n[" + triple.getLeft().name() + "](" + triple.getLeft().help() + ")", "", Registry.getCommands(c -> c.name().split(" ")[0].equals(triple.getLeft().name().split(" ")[0]), c -> !c.defaul(), c -> c.rankSufficient(rank)), user);
            }
        }
    }
    @Register(suspicion = Suspicion.RADICAL, help = "Displays the invite link for Inquisitor")
    public static void invite(Channel channel, MessageAid aid){
        aid.withContent("You must have the manage server permission to add this bot to the server.\n" +
                "You may mention me at any time as a prefix,\n" +
                "In order to let me to chat in a channel use ").withRawContent(Inquisitor.mention()).withContent(" chat true\n" +
                "https://discordapp.com/oauth2/authorize?client_id=244634255727132673&scope=bot");
    }
    @Register(help = "Displays the GitHub link to Inquisitor's repo")
    public static void gitHub(Channel channel){
        MessageHelper.send(channel, "https://github.com/nija123098/Inquisitor");
    }
    @Register(rank = Rank.GUILD_ADMIN, help = "Deletes the bot's previous messages, message count specifiable")
    public static void takeBack(Channel channel, String[] s){
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
        IUser user = Inquisitor.ourUser();
        List<IMessage> deletes = new ArrayList<>(count);
        int i = 0;
        while (count != deletes.size()){
            if (messages.get(i).getAuthor().equals(user)){
                deletes.add(messages.get(i));
            }
            ++i;
        }
        if (deletes.size() > 1){
            int[] k = {0};
            deletes.forEach(message -> RequestHandler.request(++k[0] * 500, message::delete));
        }else{
            RequestHandler.request(() -> deletes.get(0).delete());
        }
        MessageHelper.send(channel, "Deleted " + deletes.size() + " message" + (deletes.size() > 1 ? "s" : ""), 5000);
    }
    @Register(rank = Rank.BOT_ADMIN, help = "Inquisitor repeats the text")
    public static void say(Channel channel, String s){
        if (s.length() != 0){
            MessageHelper.send(channel, s);
        }
    }
    @Register(help = "Displays the Inquisitor's support server")
    public static void server(User user){
        MessageHelper.send(user, "Join the discord server here!\nhttps://discord.gg/xVA9x2H", 300000);
    }
    @Register(help = "Sets the language for this user")
    public static void setLang(User user, String s, MessageAid aid){
        if (LangHelper.setLang(user, s)){
            aid.withContent("Your language has been set to " + s);
        }else{
            aid.withContent("\"" + s  + "\" is not a supported or valid language");
        }
    }
}
