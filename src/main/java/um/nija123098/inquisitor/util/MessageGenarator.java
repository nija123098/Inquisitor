package um.nija123098.inquisitor.util;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import um.nija123098.inquisitor.bot.Inquisitor;
import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Context;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Made by nija123098 on 1/9/2017
 */
public class MessageGenarator {
    public static Future<IMessage> msg(Object...objects){
        InternalMessageBuilder builder = new InternalMessageBuilder();
        return builder.send();
    }

    private static class InternalMessageBuilder {
        String content;
        int delteTime;
        User user;
        Context context;
        Future<IMessage> send(){
            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            String c = this.content;
            MessageFuture msg = new MessageFuture();
            MessageBuilder builder = new MessageBuilder(Inquisitor.discordClient());
            if (this.context instanceof Guild){
                c = "Regarding " + ((Guild) this.context).discord().getName() + ":\n" + c;
                IUser targetUser;
                if (this.context.getData("liaison") == null){
                    targetUser = ((Guild) this.context).discord().getOwner();
                }else{
                    targetUser = User.getUserFromID(this.context.getData("liaison")).discord();
                }
                RequestHandler.request(() -> {
                    builder.withChannel(targetUser.getOrCreatePMChannel());
                    atomicBoolean.set(true);
                });
            }else if (this.context instanceof User){
                RequestHandler.request(() -> {
                    builder.withChannel(((User) this.context).discord().getOrCreatePMChannel());
                    atomicBoolean.set(true);
                });
            }else if (this.context instanceof Channel){
                builder.withChannel(this.context.getID());
            }


            RequestHandler.request(() -> msg.message = builder.send());
            return msg;
        }
    }
    private static String getLang(User user, Guild guild){
        return null;
    }
    private static class MessageFuture implements Future<IMessage> {
        private IMessage message;
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isDone() {
            return this.message != null;
        }
        @Override
        public IMessage get() {
            return this.message;
        }
        @Override
        public IMessage get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }
}
