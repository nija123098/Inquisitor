package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.ContextHelper;
import um.nija123098.inquisitor.util.Regard;

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
        if (!command){
            Registry.listen(admin, user, guild, channel, msg);
        }else{
            Method method = Registry.getCommand(msg);
            if (method != null){
                ContextHelper.execute(method, admin, user, guild, channel, msg);
            }else{
                Regard.less(() -> channel.discordChannel().sendMessage("Unrecognized Command"));
            }
        }
        /*if (msg.equals("CLOSE")){
            User.save();
            Channel.save();
            Guild.save();
            Regard.less(() -> {
                Inquisitor.inquisitor().getClient().logout();
                System.exit(7);
            });
        }else if (msg.equals("inspect roles")){
            final String[] s = {""};
            guild.discordGuild().getRoles().forEach(iRole -> s[0] += iRole.getName() + ", ");
            Regard.less(() -> user.discordUser().getOrCreatePMChannel().sendMessage(s[0]));
        }*/
    }
}
