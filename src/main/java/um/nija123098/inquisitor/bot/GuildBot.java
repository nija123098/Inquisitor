package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.command.Invoke;
import um.nija123098.inquisitor.util.StringHelper;

/**
 * Made by nija123098 on 11/5/2016
 */
public class GuildBot {
    private static final Entity PREFIX_ENTITY;
    static {
        PREFIX_ENTITY = Inquisitor.getEntity("prefixes");
    }
    private String guildID;
    public GuildBot(IDiscordClient client, String guildID) {
        this.guildID = guildID;
        client.getDispatcher().registerListener(this);
    }
    @EventSubscriber
    public void handle(MessageReceivedEvent event){
        if (!(event.getMessage().getChannel() instanceof IPrivateChannel) && event.getMessage().getChannel().getGuild().getID().equals(this.guildID)){
            String s = event.getMessage().getContent();
            s = StringHelper.limitOneSpace(s);
            boolean command = true;
            if (PREFIX_ENTITY.getData(this.guildID()) != null && s.startsWith(PREFIX_ENTITY.getData(this.guildID()))){
                s = s.substring(PREFIX_ENTITY.getData(this.guildID()).length());
            }else if (s.startsWith(Inquisitor.ourUser().mention(false))){
                s = s.substring(Inquisitor.ourUser().mention(false).length());
            }else if (s.startsWith(Inquisitor.ourUser().mention(true))){
                s = s.substring(Inquisitor.ourUser().mention(true).length());
            }else{
                command = false;
            }
            if (command && s.startsWith(" ")){
                s = s.substring(1);
            }
            if (command){
                Invoke.invoke(event.getMessage().getAuthor().getID(), this.guildID, event.getMessage().getChannel().getID(), s, event.getMessage());
            }
        }
    }
    public String guildID() {
        return this.guildID;
    }
    public void close() {
        Inquisitor.discordClient().getDispatcher().unregisterListener(this);
    }
}
