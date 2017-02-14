package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageAid;
import um.nija123098.inquisitor.util.StringHelper;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Made by nija123098 on 1/14/2017
 */
public class Time {
    @Register(defaul = true, help = "Gets the time in relation to another user, does not observe DST")
    public static Boolean time(Guild guild, String in, Entity entity, MessageAid aid){
        User other = User.getUser(in);
        if (other == null){
            aid.withToggleContent(false, "No such user ", StringHelper.addQuotes(in));
            return false;
        }
        String da = entity.getData(other);
        if (da == null){
            aid.withToggleContent(true, other.discord().getDisplayName(guild.discord()), " has not set his or her UTC offset.");
            return false;
        }
        float val = Float.parseFloat(entity.getData(other));
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar.getInstance(timeZone).add(Calendar.HOUR_OF_DAY, ((int) val));
        Calendar.getInstance(timeZone).add(Calendar.MINUTE, (int) ((val % 1) * 60));
        Calendar calendar = Calendar.getInstance(timeZone);
        aid.withToggleContent(false, "It is currently " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM") + " for ", other.discord().getDisplayName(guild.discord()));
        return true;
    }
    @Register(help = "Sets the UTC relation, ex: living in MST would mean setting the value to -7")
    public static boolean set(User user, String s, Entity entity, MessageAid aid){
        float val;
        try{val = Float.parseFloat(s);
        }catch(Exception e){
            aid.withRawContent(StringHelper.addQuotes(s) + " is not number");
            return false;
        }
        entity.putData(user, s);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar.getInstance(timeZone).add(Calendar.HOUR_OF_DAY, ((int) val));
        Calendar.getInstance(timeZone).add(Calendar.MINUTE, (int) ((val % 1) * 60));
        Calendar calendar = Calendar.getInstance(timeZone);
        aid.withRawContent("UST" + (val >= 0 ? "+" : "") + val).withContent(" set as your timezone.  It should be ").withRawContent(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
        return true;
    }
}
