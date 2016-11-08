package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.command.Invoke;

/**
 * Made by nija123098 on 11/5/2016
 */
public class GuildBot {
    private Guild guild;
    private String guildID;
    public GuildBot(IDiscordClient client, String guildID) {
        this.guildID = guildID;
        this.guild = Guild.getGuild(guildID);
        client.getDispatcher().registerListener(this);
    }
    @EventSubscriber
    public void handle(MessageReceivedEvent event){
        if (!(event.getMessage().getChannel() instanceof IPrivateChannel) && event.getMessage().getChannel().getGuild().getID().equals(this.guildID)){
            String s = event.getMessage().getContent();
            while (s.contains("  ")){
                s = s.replace("  ", " ");
            }
            boolean command = true;
            if (this.guild.getData("prefix") != null && s.startsWith(this.guild.getData("prefix"))){
                s = s.substring(this.guild.getData("prefix").length());
            }else if (s.startsWith(event.getClient().getOurUser().mention(false))){
                s = s.substring(event.getClient().getOurUser().mention(false).length());
            }else if (s.startsWith(event.getClient().getOurUser().mention(true))){
                s = s.substring(event.getClient().getOurUser().mention(true).length());
            }else{
                command = false;
            }
            if (command && s.startsWith(" ")){
                s = s.substring(1);
            }
            if (command){
                Invoke.invoke(event.getMessage().getAuthor().getID(), this.guildID, event.getMessage().getChannel().getID(), s);
            }
        }
    }
    public void close() {

    }
}
