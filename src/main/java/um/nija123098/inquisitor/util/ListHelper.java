package um.nija123098.inquisitor.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/10/2016
 */
public class ListHelper {
    public static <E> void flip(List<E> list) {
        List<E> objects = new ArrayList<>(list.size());
        for (int i = list.size() - 1; i >= 0; --i) {
            objects.add(list.get(i));
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, objects.get(i));
        }
    }
}
