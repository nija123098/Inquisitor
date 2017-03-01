package um.nija123098.inquisitor.util;

import com.darkprograms.speech.synthesiser.Synthesiser;
import javafx.util.Pair;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 2/2/2017.
 */
public class AudioHelper {
    private static final Map<String, Map<String, File>> MAP = new ConcurrentHashMap<>();
    private static final Synthesiser SYNTHESISER = new Synthesiser();
    private static final Map<IGuild, AudioPlayer> PLAYER_MAP = new ConcurrentHashMap<>();
    @SafeVarargs
    public static synchronized boolean say(String targetLang, IVoiceChannel channel, Pair<String, Boolean>...translationPairs){
        if (!channel.isConnected()){
            return false;
        }
        if (!PLAYER_MAP.containsKey(channel.getGuild())){
            PLAYER_MAP.put(channel.getGuild(), new AudioPlayer(channel.getGuild()));
        }
        try {
            PLAYER_MAP.get(channel.getGuild()).queue(synth(targetLang, translationPairs));
            return true;
        } catch (IOException | UnsupportedAudioFileException e) {// should never
            e.printStackTrace();
        }
        return false;
    }
    @SafeVarargs
    public static synchronized File synth(String targetLang, Pair<String, Boolean>...translationPairs){
        return synth(targetLang, LangHelper.getContent(targetLang, translationPairs).replace("```", ""));
    }
    public static synchronized File synth(String lang, String content){
        if (!MAP.containsKey(lang)){
            MAP.put(lang, new ConcurrentHashMap<>());
        }
        File file = MAP.get(lang).get(content);
        if (file == null){
            file = FileHelper.getTemporaryFile("mp3");
            MAP.get(lang).put(content, file);
            content = LangHelper.getContent(lang, content);
            if (lang.equals("en")){
                lang = lang + "-AU";
            }
            SYNTHESISER.setLanguage(lang);
            getSynth(file, content);
        }
        return file;
    }
    private static void getSynth(File to, String text){
        try {
            InputStream inputStream = SYNTHESISER.getMP3Data(text);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(baos.toByteArray());
            fos.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
