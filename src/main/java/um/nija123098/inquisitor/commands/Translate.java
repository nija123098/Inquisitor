package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.util.MessageAid;

/**
 * Made by nija123098 on 1/31/2017.
 */
public class Translate {
    @Register(defaul = true, emoticonAliases = "speech_balloon, speech_left", help = "Translates speech when a speech bubble is reacted to a message")
    public static Boolean translate(User user, IReaction reaction, MessageAid aid){
        if (reaction == null){
            aid.withContent("You must react with a speech bubble on a message to use this command.");
            return false;
        }
        String lang = Entity.getEntity("lang", "lang").getData(user);
        if (lang == null){
            aid.withContent("Please set your language using ").withoutTranslateContent("@Inquisitor setlang");
            return false;
        }
        IMessage message = reaction.getMessage();
        aid.withDM().withTranslate().withoutTranslateContent(message.getAuthor().getName()).withContent(" said:\n" + message.getContent());
        return true;
    }
}
