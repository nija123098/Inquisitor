package um.nija123098.inquisitor.commands;

import sx.blah.discord.handle.obj.IMessage;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 12/12/2016
 */
@Register(rank = Rank.MAKER)
public class Bot {
    @Register
    public static void reaction(String[] s, Channel channel){
        Inquisitor.getEntity("emoticons").putData(s[0], s[1]);
        MessageHelper.send(channel, s[1]);// em code, em :: code
    }
    @Register
    public static void lang(String[] s, Channel channel){
        Inquisitor.getEntity("lang").putData(s[0], s[1]);
        MessageHelper.send(channel, s[0] + " => " + s[1]);// discord code, lang code
    }
    @Register
    public static void react(IMessage message, String string){
        MessageHelper.react(string, message);
    }
    @Register
    public static void aid(MessageAid aid, String string){
        aid.withContent(string);
    }
}
