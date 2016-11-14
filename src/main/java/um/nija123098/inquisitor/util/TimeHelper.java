package um.nija123098.inquisitor.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Made by nija123098 on 11/13/2016
 */
public class TimeHelper {
    public static String currentTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        return dateFormat.format(new Date());
    }
    public static Duration between(String one, String two){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(one, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(two, formatter);
        return Duration.between(dateTime1, dateTime2);
    }
    public static String format(Duration duration){
        return duration.toString().replace("PT", "").replace("-", "").toLowerCase();
    }
}
