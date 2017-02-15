package um.nija123098.inquisitor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Made by nija123098 on 1/14/2017
 */
public class FormatHelper {
    private static final Map<String, Long> TIME_SYMBOLS = new LinkedHashMap<>();
    static {
        TIME_SYMBOLS.put("w", 604800000L);
        TIME_SYMBOLS.put("d", 86400000L);
        TIME_SYMBOLS.put("h", 3600000L);
        TIME_SYMBOLS.put("m", 60000L);
        TIME_SYMBOLS.put("s", 1000L);
    }
    public static String format(long lon){
        final AtomicLong atomicLong = new AtomicLong(lon);
        final StringBuilder builder = new StringBuilder("");
        List<Map.Entry<String, Long>> entries = new ArrayList<>(TIME_SYMBOLS.entrySet());
        Collections.reverse(entries);
        int num;
        for (int i = 0; i < TIME_SYMBOLS.size(); i++) {
            num = (int) (atomicLong.get() / entries.get(i).getValue());
            System.out.println(num + " on " + entries.get(i).getKey());
            if (num > 0){
                builder.append(num + entries.get(i).getKey());
                atomicLong.addAndGet(-num * entries.get(i).getValue());
            }
        }
        return builder.toString();
    }
    public static Long toMillis(String s){
        s = s.toLowerCase();
        long val = 0;
        String working = "";
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))){
                working += s.charAt(i);
            }else if (TIME_SYMBOLS.containsKey(s.charAt(i) + "")){
                val += Integer.parseInt(working) * TIME_SYMBOLS.get(s.charAt(i) + "");
                working = "";
            }else{
                return null;
            }
        }
        if (working.length() != 0){
            val += Integer.parseInt(working);
        }
        return val;
    }
}
