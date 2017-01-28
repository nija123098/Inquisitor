package um.nija123098.inquisitor.util;

import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.saving.Entity;

/**
 * Made by nija123098 on 1/28/2017.
 */
public class EmoticonHelper {
    private static final Entity EMOTICON_ENTITY;
    static {
        EMOTICON_ENTITY = Inquisitor.getEntity("emoticons");
    }
    public static String getEmoticon(String key){
        return EMOTICON_ENTITY.getData(key, "unknown emoticon");
    }
    public static boolean isReaction(String s){
        return EMOTICON_ENTITY.getValues().contains(s);
    }
    public static void addReaction(String key, String chars){
        EMOTICON_ENTITY.putData(key, chars);
    }
}
