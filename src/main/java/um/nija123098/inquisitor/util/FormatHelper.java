package um.nija123098.inquisitor.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 1/14/2017
 */
public class FormatHelper {
    public static String format(long time){
        int[] milliLength = {604800000, 86400000, 3600000, 60000, 1000};
        String[] lengthSymbol = {"w", "d", "h", "m", "s"};
        int[] count = new int[milliLength.length];
        for (int i = 0; i < milliLength.length; i++) {
            long sub = 0;
            for (int j = 0; j < i + 1; j++) {
                if (j != 0){
                    sub += count[j - 1] * milliLength[j - 1];
                }
            }
            count[i] = (int) ((time - sub) / milliLength[i]);
        }
        String form = "";
        for (int i = 0; i < milliLength.length; i++) {
            if (count[i] != 0){
                form += count[i] + lengthSymbol[i];
            }
        }
        return form;
    }
    private static final Map<Character, Long> TIME_SYMBOLS = new ConcurrentHashMap<>();
    static {
        TIME_SYMBOLS.put('w', 604800000L);
        TIME_SYMBOLS.put('d', 86400000L);
        TIME_SYMBOLS.put('h', 3600000L);
        TIME_SYMBOLS.put('m', 60000L);
        TIME_SYMBOLS.put('s', 1000L);
    }
    public static Long toMillis(String s){
        s = s.toLowerCase();
        long val = 0;
        String working = "";
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))){
                working += s.charAt(i);
            }else if (TIME_SYMBOLS.containsKey(s.charAt(i))){
                val += Integer.parseInt(working) * TIME_SYMBOLS.get(s.charAt(i));
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
