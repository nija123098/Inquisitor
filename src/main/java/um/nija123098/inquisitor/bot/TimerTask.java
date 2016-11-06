package um.nija123098.inquisitor.bot;

import java.util.List;

/**
 * Made by nija123098 on 11/6/2016
 */
@FunctionalInterface
public interface TimerTask {
    void tick();
    default List<String> close(){
        return null;
    }
}
