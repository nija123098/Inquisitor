package um.nija123098.inquisitor.commands;

import javafx.util.Pair;
import sx.blah.discord.handle.obj.Status;
import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 12/10/2016
 */
@Register(name = "status", rank = Rank.BOT_ADMIN)
public class Stat {
    @Register(override = true, name = "status startup", startup = true, rank = Rank.NONE, help = "Starts status message cycling")
    public static void startup(Entity entity){
        RequestHandler.request(() -> {
            Inquisitor.discordClient().changeStatus(Status.game("with the login screen"));
            changePlayText(entity);
        });
    }
    private static void changePlayText(Entity entity){
        RequestHandler.request(60000, () -> {
            if (Inquisitor.getLockdown()){
                return;
            }
            String[] options = entity.getData("play_text", "").split(":");
            Inquisitor.discordClient().getShards().forEach(iShard -> iShard.changeStatus(Status.game(options[Rand.integer(options.length - 1)])));
            changePlayText(entity);
        });
    }
    @Register(defaul = true, help = "Lists all play text possibilities")
    public static void status(User user, Channel channel, Entity entity){
        MessageHelper.checkYourDMs(channel, user);
        CommonMessageHelper.displayList("# play text possibilities", "", StringHelper.getList(entity.getData("play_text", "").split(":")), user);
    }
    @Register(help = "Adds play text")
    public static Boolean add(Channel channel, Entity entity, String s){
        if (s.length() == 0){
            MessageHelper.send(channel, "Can not add a string of length 0");
            return false;
        }
        entity.putData("play_text", entity.getData("play_text", "") + s + ":");
        MessageHelper.send(channel, "Added playing text " + StringHelper.addQuotes(s));
        return true;
    }
    @Register(help = "Removes the text specified")
    public static Boolean remove(Channel channel, Entity entity, String s){
        if (entity.getData("play_text").contains(s)){
            entity.putData("play_text", entity.getData("play_text", "").replace(s + ":", ""));
            MessageHelper.send(channel, "Removed playing text " + StringHelper.addQuotes(s));
        }else{
            MessageHelper.send(channel, "No playing text " + StringHelper.addQuotes(s));
            return false;
        }
        return true;
    }
    @Register(help = "Removes the text by index")
    public static Boolean removeIndex(Channel channel, Entity entity, String s){
        String[] options = entity.getData("play_text", "").split(":");
        int[] i = new int[1];
        try{i[0] = Integer.parseInt(s);
        }catch(Exception e){
            MessageHelper.send(channel, "No such number " + StringHelper.addQuotes(s));
            return false;
        }
        if (i[0] > options.length){
            MessageHelper.send(channel, i[0] + " is too large of a number, the index goes up to " + (options.length - 1));
            return false;
        }
        entity.putData("play_text", entity.getData("play_text").replace(options[i[0]] + ":", ""));
        MessageHelper.send(channel, "Removed play text " + StringHelper.addQuotes(options[i[0]]));
        return true;
    }
    @Register(help = "Lists all play text messages")
    public static void list(User user, Entity entity){
        CommonMessageHelper.displayList("# A list of play text displays", "", StringHelper.getList(entity.getData("play_text", "").split(":")), user);
    }
    private static final List<Pair<String, Function<String, String>>> PAIRS;
    static {
        PAIRS = new ArrayList<>();
        PAIRS.add(new Pair<>("rand", s -> {
            try{return Rand.integer(Integer.parseInt(s)) + "";
            }catch(Exception e){return "rand";}
        }));
        PAIRS.add(new Pair<>("shards", s -> Inquisitor.discordClient().getShardCount() + ""));
        PAIRS.add(new Pair<>("guilds", s -> Inquisitor.discordClient().getGuilds().size() + ""));
        PAIRS.add(new Pair<>("users", s -> Inquisitor.discordClient().getUsers().size() + ""));
        PAIRS.add(new Pair<>("commands", s -> Registry.getCommands().size() + ""));
    }
    private static String format(String string){
        String s = "";
        String[] strings = string.split(" ");
        for (int i = 0; i < strings.length; i++) {
            if (string.endsWith(">")){
                for (Pair<String, Function<String, String>> pair : PAIRS){
                    if (strings[i].startsWith(pair.getKey() + "<") && strings[i].endsWith(">")){
                        s += pair.getValue().apply(strings[i].replace(pair.getKey() + "<", "").replace(">", ""));
                        break;
                    }
                }
            }else{
                s += strings[i];
            }
        }
        return s;
    }
}
