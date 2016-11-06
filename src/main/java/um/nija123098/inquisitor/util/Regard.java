package um.nija123098.inquisitor.util;

/**
 * Made by nija123098 on 11/5/2016
 */
@FunctionalInterface
public interface Regard {
    void less() throws Throwable;
    static void less(Regard regard){
        try{regard.less();
        }catch(Throwable ignored){ignored.printStackTrace();}
    }
}
