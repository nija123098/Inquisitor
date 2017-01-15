package um.nija123098.inquisitor.util;

import javafx.util.Pair;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.util.Arrays;
import java.util.List;

/**
 * Made by nija123098 on 1/13/2017
 */
public class LangHelper {
    private static final List<String> LANGS;
    private static final Entity LANG_ENTITY;
    static {
        LANGS = Arrays.asList("en-us", "en-au");
        LANG_ENTITY = Entity.getEntity("lang", "lang");
    }
    public static Pair<String, Boolean> getLang(User user, Guild guild){
        String s = LANG_ENTITY.getData(user.getID());
        if (s != null){
            return new Pair<String, Boolean>(s, true);
        }
        return new Pair<String, Boolean>(LANG_ENTITY.getData(guild.getID()), false);
    }
    public static boolean isLang(String s){
        return LANGS.contains(s);
    }
}
