package um.nija123098.inquisitor.util;

/**
 * Made by nija123098 on 1/14/2017
 */
public class FormatHelper {
    public static String format(long time){
        int[] milliLength = {604800000, 86400000, 3600000, 60000, 1000};
        String[] lenghtSymbol = {"w", "d", "h", "m", "s"};
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
                form += count[i] + lenghtSymbol[i];
            }
        }
        return form;
    }
}
