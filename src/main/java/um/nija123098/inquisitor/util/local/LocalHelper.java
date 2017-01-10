package um.nija123098.inquisitor.util.local;

import com.darkprograms.speech.synthesiser.Synthesiser;
import sx.blah.discord.handle.obj.IRegion;
import um.nija123098.inquisitor.bot.Entity;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.util.FileHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        String comp = s.trim().toLowerCase();
        for (Country country : countries) {
            if (s.equals(country.emoticonChars) || s.equals(country.discordCode) || comp.equals(country.compName)){
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
            countries.add(new Country(dat[0], dat[1], dat[2], dat[3]));
        });
    }
    public static class Country {
        private String discordCode, emoticonChars, langCode, name, compName;
        public Country(String discordCode, String emoticonChars, String langCode, String name) {
            this.discordCode = discordCode;
            this.emoticonChars = emoticonChars;
            this.langCode = langCode;
            this.name = name;
            this.compName = name.trim().toLowerCase();
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
        public String getName() {
            return this.name;
        }
    }
    public static Country getBestGuess(IRegion region){
        switch (region.getName()){

        }
        return null;
    }
}
