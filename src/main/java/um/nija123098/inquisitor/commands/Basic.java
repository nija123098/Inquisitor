package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageList;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.*;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.Log;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/7/2016
 */
@Register(natural = true)
public class Basic {
    @Register(hidden = true)
    public static void ping(Channel channel){
        MessageHelper.send(channel, "pong");
        Log.info("pong");
    }
    @Register(help = "Lists information on Inquisitor")
    public static void info(Channel channel){
        MessageHelper.send(channel, "" +
                "This is " + Inquisitor.discordClient().getOurUser().mention(false) + " is a Discord info bot.\n" +
                "It collects and displays information on servers, users, and bots for a benevolent purpose.\n" +
                Inquisitor.discordClient().getOurUser().mention() + " is made by nija123098#7242");
    }
    @Register(rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL, help = "Saves all bot configuration files")
    public static void save(Channel channel){
        Inquisitor.save();
        MessageHelper.send(channel, "*Files saved*");
    }
    @Register(rank = Rank.BOT_ADMIN, suspicion = Suspicion.HERETICAL, help = "Shuts down the bot without restart")
    public static void close(User user){
        Log.warn(user.discord().getName() + " is closing Inquisitor");
        Inquisitor.close();
    }
    @Register(help = "Displays all commands or help on a specific command by help <command>")
    public static void help(Channel channel, User user, Guild guild, String s){
        if (s.equals("")){
            Rank rank = Rank.getRank(user, guild);
            if (guild != null){
                MessageHelper.send(channel, user.discord().mention() + " check your DMs!");
            }
            CommonMessageHelper.displayHelp("# Help at rank " + rank.name().replace("_", " "), "", Registry.getCommands(command -> command.rankSufficient(rank), Command::surface, command -> !command.hidden()), user);
        }else{
            Command command = Registry.getCommand(s);
            if (command != null){
                MessageHelper.send(channel, command.help());
            }else{
                MessageHelper.send(channel, "No such command");
            }
        }
    }
    @Register(help = "Displays the invite link for Inquisitor")
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
