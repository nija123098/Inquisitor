package um.nija123098.inquisitor.util;

import com.darkprograms.speech.synthesiser.Synthesiser;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Made by nija123098 on 1/6/2017
 */
public class LocalHelper {
    private static final Synthesiser synthesiser = new Synthesiser("en-US");
    private static final Map<String, File> files = new ConcurrentHashMap<String, File>();
    public static File getSpeech(String text){
        File to = files.get(text);
        if (to != null){
            return to;
        }else{
            to = new File(FileHelper.getJarContainer() + "\\temp\\audioTemp" + files.size() + ".mp3");
            to.deleteOnExit();
        }
        InputStream inputStream;
        try{inputStream = synthesiser.getMP3Data(text);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(baos.toByteArray());
            fos.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        files.put(text, to);
        return to;
    }
    public static Country getCountry(String s){
        for (Country country : countries) {
            if (s.equals(country.emoticonChars)){
                return country;
            }
        }
        return null;
    }
    private static final List<Country> countries = new CopyOnWriteArrayList<Country>();
    static {
        Entity entity = Inquisitor.getEntity("lang");
        entity.getSaved().forEach(s -> {
            String[] dat = entity.getData(s).split(":");
            countries.add(new Country(dat[0], dat[1], dat[2]));
        });
    }
    public static class Country {
        private String discordCode, emoticonChars, langCode;
        public Country(String discordCode, String emoticonChars, String langCode) {
            this.discordCode = discordCode;
            this.emoticonChars = emoticonChars;
            this.langCode = langCode;
        }
        public String getDiscordCode(){
            return this.discordCode;
        }
        public String getFlag(){
            return this.emoticonChars;
        }
        public String getLangCode(){
            return this.langCode;
        }
    }
}
