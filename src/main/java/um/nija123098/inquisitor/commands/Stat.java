package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.CommonMessageHelper;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;
import um.nija123098.inquisitor.util.Rand;
import um.nija123098.inquisitor.util.RequestHandler;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 12/10/2016
 */
@Register(name = "status", rank = Rank.BOT_ADMIN)
public class Stat {
    @Register(override = true, name = "status startup", startup = true, rank = Rank.NONE, help = "Starts status message cycling")
    public static void startup(Entity entity){
        RequestHandler.request(() -> {
            Inquisitor.discordClient().online("with the login screen");
            changePlayText(entity);
        });
    }
    private static void changePlayText(Entity entity){
        RequestHandler.request(60000, () -> {
            if (Inquisitor.getLockdown()){
                return;
            }
            String[] options = entity.getData("play_text", "").split(":");
            Inquisitor.discordClient().online(options[Rand.integer(options.length - 1)]);
            changePlayText(entity);
        });
    }
    @Register(defaul = true, help = "Lists all play text possibilities")
    public static void status(User user, Channel channel, Entity entity){
        MessageHelper.checkYourDMs(channel, user);
        CommonMessageHelper.displayList("# play text possibilities", "", StringHelper.getList(entity.getData("play_text", "").split(":")), user);
    }
    @Register(help = "Adds play text")
    public static Boolean add(Channel channel, Entity entity, String s, MessageAid aid){
        if (s.length() == 0){
            aid.withContent("Can not add a string of length 0");
            return false;
        }
        entity.putData("play_text", entity.getData("play_text", "") + s + ":");
        aid.withContent("Added playing text ").withRawContent(StringHelper.addQuotes(s));
        return true;
    }
    @Register(help = "Removes the text specified")
    public static Boolean remove(Entity entity, String s, MessageAid aid){
        if (entity.getData("play_text").contains(s)){
            entity.putData("play_text", entity.getData("play_text", "").replace(s + ":", ""));
            aid.withContent("Removed playing text ").withRawContent(StringHelper.addQuotes(s));
            return true;
        }
        aid.withContent("No playing text ").withRawContent(StringHelper.addQuotes(s));
        return false;
    }
    @Register(help = "Removes the text by index")
    public static Boolean removeIndex(Entity entity, String s, MessageAid aid){
        String[] options = entity.getData("play_text", "").split(":");
        int[] i = new int[1];
        try{i[0] = Integer.parseInt(s);
        }catch(Exception e){
            aid.withContent("No such number ").withRawContent(StringHelper.addQuotes(s));
            return false;
        }
        if (i[0] > options.length){
            aid.withContent(i[0] + " is too large of a number, the index goes up to " + (options.length - 1));
            return false;
        }
        entity.putData("play_text", entity.getData("play_text").replace(options[i[0]] + ":", ""));
        aid.withContent("Removed play text ").withRawContent(StringHelper.addQuotes(options[i[0]]));
        return true;
    }
    @Register(guaranteedSuccess = true, help = "Lists all play text messages")
    public static void list(User user, Entity entity){
        CommonMessageHelper.displayList("# A list of play text displays", "", StringHelper.getList(entity.getData("play_text", "").split(":")), user);
    }
}
