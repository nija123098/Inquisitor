package um.nija123098.inquisitor.commands;

import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.command.Register;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Made by nija123098 on 1/14/2017
 */
public class Time {
    @Register(defaul = true, help = "Gets the time in relation to another user, does not observe DST")
    public static Boolean time(Guild guild, Channel channel, String in, Entity entity){
        User other = User.getUser(in);
        if (other == null){
            MessageHelper.send(channel, "No such user \"" + in + "\"");
            return false;
        }
        String da = entity.getData(other.getID());
        if (da == null){
            MessageHelper.send(channel, other.discord().getDisplayName(guild.discord()) + " has not set his or her UTC offset.");
            return false;
        }
        float val = Float.parseFloat(entity.getData(other.getID()));
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar.getInstance(timeZone).add(Calendar.HOUR_OF_DAY, ((int) val));
        Calendar.getInstance(timeZone).add(Calendar.MINUTE, (int) ((val % 1) * 60));
        Calendar calendar = Calendar.getInstance(timeZone);
        MessageHelper.send(channel, "It is currently " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM") + " for " + other.discord().getDisplayName(guild.discord()));
        return true;
    }
    @Register(help = "Sets the UTC relation, ex: living in MST would mean setting the value to -7")
    public static boolean set(User user, Channel channel, String s, Entity entity){
        float val;
        try{val = Float.parseFloat(s);
        }catch(Exception e){
            return false;
        }
        entity.putData(user.getID(), s);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar.getInstance(timeZone).add(Calendar.HOUR_OF_DAY, ((int) val));
        Calendar.getInstance(timeZone).add(Calendar.MINUTE, (int) ((val % 1) * 60));
        Calendar calendar = Calendar.getInstance(timeZone);
        MessageHelper.send(channel, "UST" + (val >= 0 ? "+" : "") + val + " set as your timezone.  It should be " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
        return true;
    }

    public static void main(String[] args) {
        ZoneId.getAvailableZoneIds().forEach(System.out::println);
    }
}
