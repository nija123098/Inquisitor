package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.command.Rank;
import um.nija123098.inquisitor.command.Register;

/**
 * Made by nija123098 on 11/20/2016
 */
@Register(natural = true, rank = Rank.BOT_ADMIN)
public class Admin {
    @Register
    public static void lockdown(){
        Inquisitor.lockdown();
        Inquisitor.discordClient().changePresence(true);
    }
}
