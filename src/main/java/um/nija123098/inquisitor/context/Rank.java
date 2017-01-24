package um.nija123098.inquisitor.context;

import sx.blah.discord.handle.obj.Permissions;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;

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
    NONE,;
    private static Entity ranks = Inquisitor.getEntity("permissions");
    public static Rank getRank(User user, Guild guild){
        if (user.discord().getID().equals("191677220027236352")){
            return MAKER;
        }
        if ((":" + ranks.getData("banned")).contains(user.getID())){
            return BANNED;
        }
        if ((":" + ranks.getData("admin")).contains(user.getID())){
            return BOT_ADMIN;
        }
        if (guild != null){
            if (guild.discord().getOwner().equals(user.discord())){
                return GUILD_OWNER;
            }
            if (user.discord().getPermissionsForGuild(guild.discord()).contains(Permissions.ADMINISTRATOR)){
                return GUILD_ADMIN;
            }
        }
        if (user.discord().isBot()){
            return BOT;
        }
        return USER;
    }
    public static String getRankName(User user, Guild guild){
        Rank rank = getRank(user, guild);
        String name = rank.name(), build = name.charAt(0) + "";
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i - 1) == '_'){
                build += name.charAt(i);
            }else{
                build += name.toLowerCase().charAt(i);
            }
        }
        return build.replace("_", " ");
    }
    public static boolean isSufficient(Rank required, Rank level){
        return level.ordinal() >= required.ordinal();
    }
}
