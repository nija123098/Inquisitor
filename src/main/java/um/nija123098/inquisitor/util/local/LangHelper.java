package um.nija123098.inquisitor.util.local;

import com.darkprograms.speech.translator.GoogleTranslate;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 1/9/2017
 */
public class LangHelper {
    private static final Map<Pair<String, String>, String> MAP = new ConcurrentHashMap<Pair<String, String>, String>();
    public static String translate(String lang, String com){
        Pair<String, String> pair = new Pair<String, String>(lang, com);
        if (!MAP.containsKey(pair)){
            try{MAP.put(pair, GoogleTranslate.translate(lang, com));
            }catch(IOException e){e.printStackTrace();}
        }
        return MAP.get(pair);
    }
}
