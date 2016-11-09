package um.nija123098.inquisitor.command;

import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

/**
 * Made by nija123098 on 11/7/2016
 */
public enum Rank {
    BANNED,
    BOT,
    USER,
    GUILD_ADMIN,
    GUILD_OWNER,
    BOT_ADMIN,
    MAKER,
    @Deprecated
    NONE,;
    public static Rank getRank(User user, Guild guild){
        if (user.user().getID().equals("191677220027236352")){
            return MAKER;
        }
        if ("true".equals(user.getData("banned"))){
            return BANNED;
        }
        if ("true".equals(user.getData("admin"))){
            return BOT_ADMIN;
        }
        if (guild != null){
            if (guild.guild().getOwner().equals(user.user())){
                return GUILD_OWNER;
            }
            for (IRole iRole : guild.guild().getRolesForUser(user.user())) {
                if (iRole.getPermissions().contains(Permissions.ADMINISTRATOR)){
                    return GUILD_ADMIN;
                }
            }
        }
        if (user.user().isBot()){
            return BOT;
        }
        return USER;
    }
}
