package um.nija123098.inquisitor.context;

import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 11/12/2016
 */
public enum Suspicion {
    HERETICAL(1000),
    RADICAL(100),
    DEVIANT(10),
    ORTHODOX(-10),
    LOYAL(-100),
    SAINTLY(-1000),
    ENLIGHTENED(Long.MIN_VALUE),;
    Suspicion(long mini){
        this.min = mini;
    }
    private final long min;
    public static Suspicion getLevel(User user){
        return getLevel(Float.parseFloat(user.getData("suspicion", "0")));
    }
    public static Suspicion getLevel(float level){
        for (Suspicion suspicion : Suspicion.values()) {
            if (level > suspicion.min){
                return suspicion;
            }
        }
        return ORTHODOX;
    }
    public static void addLevel(User user, float delta, Channel channel, boolean message){
        float level = Float.parseFloat(user.getData("suspicion", "0"));
        Suspicion suspicion = Suspicion.getLevel(level);
        float newLevel = level + delta;
        user.putData("suspicion", newLevel + "");
        Suspicion newSuspicion = Suspicion.getLevel(newLevel);
        if (suspicion != newSuspicion && message){
            if (channel != null){
                MessageHelper.send(channel, user.discord().mention() + " you are now considered " + newSuspicion);
            }else{
                MessageHelper.send(user, user.discord().mention() + " you are now considered " + newSuspicion);
            }
        }
    }
    public static boolean isSufficient(Suspicion target, Suspicion level){
        return target.ordinal() >= level.ordinal();
    }
}
