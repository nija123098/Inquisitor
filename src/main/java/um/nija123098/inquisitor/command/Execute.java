package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.ContextHelper;
import um.nija123098.inquisitor.util.RequestHandler;
import um.nija123098.inquisitor.util.StringHelper;

import java.lang.reflect.Method;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Execute {
    public static void execute(boolean command, boolean admin, String user, String guild, String channel, String msg){
        if (guild == null){
            execute(command, admin, User.getUser(user), null, Channel.getChannel(channel), msg);
        }else{
            execute(command, admin, User.getUser(user), Guild.getGuild(guild), Channel.getChannel(channel), msg);
        }
    }
    private static void execute(boolean command, boolean admin, User user, Guild guild, Channel channel, String msg){
        msg = StringHelper.limitOneSpace(msg);
        if (!command){
            Registry.listen(admin, user, guild, channel, msg);
        }else{
            Method method = Registry.getCommand(msg);
            if (method != null){
                msg = msg.toLowerCase().replace((method.isAnnotationPresent(Natural.class) ? "" : method.getDeclaringClass().getSimpleName().toLowerCase() + " ") + method.getName().toLowerCase(), "");
                if (msg.startsWith(" ")){
                    msg = msg.substring(1);
                }
                ContextHelper.execute(method, admin, user, guild, channel, msg);
            }else{
                RequestHandler.request(() -> channel.discordChannel().sendMessage("Unrecognized Command"));
            }
        }
    }
}
