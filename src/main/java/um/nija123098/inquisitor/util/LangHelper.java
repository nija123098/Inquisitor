package um.nija123098.inquisitor.util;

import javafx.util.Pair;
import org.json.JSONArray;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Made by nija123098 on 1/13/2017
 */
public class LangHelper {
    private static final List<String> LANGS;
    private static final Entity LANG_ENTITY;
    private static final Map<String, List<Pair<String, String>>> LANG_CONTENT;
    static {
        LANGS = Arrays.asList("en-us", "en-au");
        LANG_ENTITY = Entity.getEntity("lang", "lang");
        LANG_CONTENT = new HashMap<String, List<Pair<String,String>>>();
    }
    public static Pair<String, Boolean> getLang(User user, Guild guild){
        String s = LANG_ENTITY.getData(user.getID());
        if (s != null){
            return new Pair<String, Boolean>(s, true);
        }
        return new Pair<String, Boolean>(LANG_ENTITY.getData(guild.getID(), "en-us"), false);
    }
    public static boolean isLang(String s){
        return LANGS.contains(s);
    }
    public static synchronized String getContent(String lang, String content){
        if (!LANG_CONTENT.containsKey(lang)){
            LANG_CONTENT.put(lang, new ArrayList<Pair<String, String>>());
        }
        List<Pair<String, String>> lan = LANG_CONTENT.get(lang);
        for (Pair<String, String> pair : lan) {
            if (pair.getKey().equals(lang)){
                return pair.getValue();
            }
        }
        String result = translate(lang, content);
        lan.add(new Pair<String, String>(content, result));
        return result;
    }
    public static String translate(String isoTo, String content){
        try {
            return get(isoTo, content);
        } catch (Exception e){
            return content;
        }
    }
    private static String get(String l, String c) throws Exception{
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + l + "&dt=t&q=" + URLEncoder.encode(c, "UTF-8");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONArray jsonArray = new JSONArray(response.toString());
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
        return jsonArray3.get(0).toString();
    }
}
