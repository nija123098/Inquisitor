package um.nija123098.inquisitor.util;

import java.util.Random;

/**
 * Made by nija123098 on 12/10/2016
 */
public class Rand {
    private static final Random RANDOM = new Random();
    public static int integer(int max){
        if (max == 0){
            return 0;
        }
        return RANDOM.nextInt(max + 1);
    }
}
