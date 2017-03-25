package um.nija123098.inquisitor.util;

import javafx.util.Pair;
import org.json.JSONArray;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Context;
import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Made by nija123098 on 1/13/2017
 */
public class LangHelper {
    private static final Entity LANGS;
    private static final Entity LANG_ENTITY;
    private static final Map<String, Map<String, String>> LANG_CONTENT;
    static {
        LANG_ENTITY = Entity.getEntity("lang", "lang");
        LANGS = Entity.getEntity("lang", "langcodes");
        LANG_CONTENT = new HashMap<>();
    }
    public static Pair<String, Boolean> getLang(User user, Guild guild){
        String s = LANG_ENTITY.getData(user);
        if (s != null){
            return new Pair<>(s, true);
        }
        if (guild != null){
            return new Pair<>(LANG_ENTITY.getData(guild, "en"), false);
        }
        return new Pair<>("en", false);
    }
    public static boolean setLang(Context context, String code){
        code = code.toLowerCase();
        if (context instanceof Channel){// not supported, yet
            return false;
        }
        if (LANGS.getSaved().contains(code)){
            code = LANGS.getData(code);
        }
        boolean isLang = LANGS.getValues().contains(code);
        if (isLang){
            LANG_ENTITY.putData(context, code);
        }
        return isLang;
    }
    public static void addLanguage(String name, String code) {
        LANGS.putData(name.toLowerCase(), code.toLowerCase());
    }
    @SafeVarargs
    public static synchronized String getContent(String lang, Pair<String, Boolean>...translationPairs){
        String building = "";
        for (Pair<String, Boolean> translationPair : translationPairs) {
            building += translationPair.getValue() ? getContent(lang, translationPair.getKey()) : translationPair.getKey();
        }
        return building;
    }
    public static synchronized String getContent(String lang, String content){
        String[] contents = content.split("\n");
        content = "";
        for (String c : contents) {
            content += getSingleContent(lang, c) + "\n";
        }
        return content.substring(0, content.length() - 1);
    }
    private static synchronized String getSingleContent(String lang, String content){
        Map<String, String> lan = LANG_CONTENT.computeIfAbsent(lang, s -> new HashMap());
        String translate = lan.get(content);
        if (translate != null) {
            return translate;
        }
        int before = 0, after = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) != ' '){
                before = i;
                break;
            }
        }
        for (int i = content.length() - 1; i > -1; --i) {
            if (content.charAt(i) != ' '){
                after = content.length() - i - 1;
                break;
            }
        }
        content = content.substring(before, content.length() - after);
        translate = translate(lang, content);
        for (int i = 0; i < before; i++) {
            translate = " " + translate;
        }
        for (int i = 0; i < before; i++) {
            translate += " ";
        }
        lan.put(content, translate);
        return translate;
    }
    private static String translate(String isoTo, String content){
        try {
            return get(isoTo, content);
        } catch (Exception e){
            Log.error("Translation exception: " + content);
            e.printStackTrace();
            return content;
        }
    }
    private static String get(String l, String c) throws Exception{
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + l + "&dt=t&q=" + URLEncoder.encode(c, "UTF-8");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
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
