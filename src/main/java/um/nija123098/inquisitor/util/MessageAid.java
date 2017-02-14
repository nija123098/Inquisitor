package um.nija123098.inquisitor.util;

import javafx.util.Pair;
import sun.plugin2.message.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Made by nija123098 on 1/18/2017
 */
public class MessageAid {
    private User user;
    private final Channel channel;
    private Guild guild;
    private final MessageBuilder internal;
    private boolean checkMessages, priv, translate, noSpace, override;
    private int delete;
    private final List<Pair<String, Boolean>> contents;
    public MessageAid(User user, Channel channel, Guild guild){
        this.user = user;
        this.channel = channel;
        if (guild == null){
            this.priv = true;
        }else{
            this.guild = guild;
        }
        this.internal = new MessageBuilder(Inquisitor.discordClient());
        this.noSpace = true;
        this.translate = false;
        this.contents = new ArrayList<>();
    }
    public MessageAid withTTS(){
        this.internal.withTTS();
        return this;
    }
    public MessageAid withCheck(){
        this.checkMessages = true;
        return this;
    }
    public MessageAid withDM(){
        this.priv = true;
        return this;
    }
    public MessageAid withGuild(){
        this.priv = true;
        String liaison = guild.getData("liaison");
        if (liaison != null){
            this.user = User.getUserFromID(liaison);
        }
        return this;
    }
    public MessageAid withContent(String s){
        this.contents.add(new Pair<>(s, true));
        return this;
    }
    public MessageAid withRawContent(String s){
        this.contents.add(new Pair<>(s, false));
        return this;
    }
    public MessageAid withToggleContent(boolean rawFirst, String...s){
        for (String st : s){
            rawFirst = !rawFirst;
            if (rawFirst){
                this.withRawContent(st);
            }else{
                this.withContent(st);
            }
        }
        return this;
    }
    public MessageAid withDelete(int delay){
        this.delete = delay;
        return this;
    }
    public MessageAid withTranslate(){
        this.translate = true;
        return this;
    }
    public MessageAid withoutNoSpace(){
        this.noSpace = false;
        return this;
    }
    public MessageAid withOverride(){
        this.override = true;
        return this;
    }
    public void send(){
        if (this.contents.size() == 0){
            return;
        }
        String content = "";
        Pair<String, Boolean> langPair = LangHelper.getLang(this.user, this.guild);
        if (this.translate || !langPair.getKey().equals("en")){
            if (!langPair.getValue()){
                this.withContent("\nYou can set your language of preference with ").withRawContent("@Inquisitor setlang").withContent(" <language name>");
            }
            content = LangHelper.getContent(langPair.getKey(), this.contents.toArray(new Pair[this.contents.size()]));
        }else{
            for (Pair<String, Boolean> c : this.contents) {
                content += c.getKey();
            }
        }
        final AtomicReference<IChannel> channel = new AtomicReference<>(this.channel.discord());
        boolean pubAllowed = this.channel.isPrivate() || this.override;
        final AtomicBoolean channelMade = new AtomicBoolean(true);
        if (!pubAllowed){
            pubAllowed = "true".equals(this.channel.getData("chat_approved", "false"));
        }
        if (this.checkMessages){
            this.priv = true;
            if (pubAllowed){
                RequestBuffer.request(() -> {
                    try {
                        IMessage message = new MessageBuilder(Inquisitor.discordClient()).withChannel(this.channel.getID()).withContent("\u200B<@" + this.user.getID() + "> check you messages!").send();
                        RequestHandler.request(20000, message::delete);
                    } catch (MissingPermissionsException e) {
                        this.priv = false;
                    } catch (DiscordException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        if (!this.priv && (!pubAllowed)){
            this.priv = true;
            this.withContent("\nI have not been granted permission to speak in that channel, so I have DMed you instead.");
        }
        if (this.priv){
            channelMade.set(false);
            RequestBuffer.request(() -> {
                try {
                    channel.set(this.user.discord().getOrCreatePMChannel());
                    channelMade.set(true);
                } catch (DiscordException e) {
                    e.printStackTrace();
                }
            });
        }
        final String finalContent = (this.noSpace ? "\u200B" : "") + content;
        RequestBuffer.request(() -> {
            if (!channelMade.get()){
                throw new RateLimitException("Channel making MessageAid dodge", 100, "red", false);
            }
            try {
                IMessage message = this.internal.withChannel(channel.get()).withContent(finalContent).send();
                if (this.delete != 0){
                    RequestHandler.request(this.delete, message::delete);
                }
            } catch (DiscordException e) {
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                Log.error("Could not pm user: " + this.user.discord().getName() + "#" + this.user.discord().getDiscriminator());
            } catch (Throwable t){
                t.printStackTrace();
            }
        });
    }
}
