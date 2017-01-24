package um.nija123098.inquisitor.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.*;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Made by nija123098 on 1/18/2017
 */
public class MessageAid {
    private User user;
    private Channel channel;
    private Guild guild;
    private String content;
    private MessageBuilder internal;
    private boolean checkMessages, priv, edited;
    private int delete;
    public MessageAid(User user, Channel channel, Guild guild){
        this.user = user;
        this.channel = channel;
        if (guild == null){
            this.priv = true;
        }else{
            this.guild = guild;
        }
        this.content = "";
        this.internal = new MessageBuilder(Inquisitor.discordClient());
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
        this.content += s;
        this.edited = true;
        return this;
    }
    public MessageAid withDelete(int delay){
        this.delete = delay;
        return this;
    }
    public void send(){
        if (!this.edited){
            return;
        }
        this.setContent();
        final AtomicReference<IChannel> channel = new AtomicReference<>(this.channel.discord());
        boolean pubAllowed = this.channel.isPrivate();
        final AtomicBoolean channelMade = new AtomicBoolean(true);
        if (!pubAllowed){
            pubAllowed = "true".equals(this.channel.getData("chat_approved", "false"));
        }
        if (this.checkMessages){
            this.priv = true;
            if (pubAllowed){
                RequestBuffer.request(() -> {
                    try {
                        IMessage message = new MessageBuilder(Inquisitor.discordClient()).withChannel(this.channel.getID()).withContent("<@" + this.user.getID() + "> check you messages!").send();
                        RequestHandler.request(20000, message::delete);
                    } catch (MissingPermissionsException e) {
                        this.priv = false;
                    } catch (DiscordException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        if (!this.priv && !pubAllowed){
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
        RequestBuffer.request(() -> {
            if (!channelMade.get()){
                throw new RateLimitException("Channel Made MessageAid dodge", 100, "red", false);
            }
            try {
                IMessage message = this.internal.withChannel(channel.get()).withContent(this.content).send();
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
    private void setContent(){
        //this.content = LangHelper.getContent(LangHelper.getLang(this.user, this.guild).getKey(), this.content);
    }
}
