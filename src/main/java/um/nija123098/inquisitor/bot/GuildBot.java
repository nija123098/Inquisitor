package um.nija123098.inquisitor.bot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import um.nija123098.inquisitor.command.Execute;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.util.ContextHelper;

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
            boolean command = false;
            if (this.guild.getData("prefix") != null && s.startsWith(this.guild.getData("prefix"))){
                command = true;
                s = s.substring(this.guild.getData("prefix").length());
            }else if (s.startsWith(event.getClient().getOurUser().mention(false))){
                command = true;
                s = s.substring(event.getClient().getOurUser().mention(false).length());
            }else if (s.startsWith(event.getClient().getOurUser().mention(true))){
                command = true;
                s = s.substring(event.getClient().getOurUser().mention(true).length());
            }
            if (command){
                while (s.startsWith(" ")){
                    s = s.substring(1);
                }
            }
            Execute.execute(command, ContextHelper.isAdmin(event.getMessage().getAuthor(), event.getMessage().getGuild()), event.getMessage().getAuthor().getID(), this.guildID, event.getMessage().getChannel().getID(), s);
        }
    }
    public void close() {

    }
}
