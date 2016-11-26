package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Channel;
import um.nija123098.inquisitor.context.Context;
import um.nija123098.inquisitor.context.Guild;
import um.nija123098.inquisitor.context.User;
import um.nija123098.inquisitor.util.MessageHelper;

/**
 * Made by nija123098 on 11/26/2016
 */
public class ExitCommandException extends RuntimeException {
    private Context context;
    private String message;
    private boolean executed, override;
    public ExitCommandException(){
        this.executed = false;
    }
    public ExitCommandException(boolean executed){
        this.executed = executed;
    }
    public ExitCommandException(Context context, String message){
        this.context = context;
        this.message = message;
    }
    public ExitCommandException(boolean executed, Context context, String message){
        this.executed = executed;
        this.context = context;
        this.message = message;
    }
    public ExitCommandException(Context context, String message, boolean override){
        this.context = context;
        this.message = message;
        this.override = override;
    }
    public ExitCommandException(boolean executed, Context context, String message, boolean override){
        this.executed = executed;
        this.context = context;
        this.message = message;
        this.override = override;
    }
    public void sendMessage(){
        if (this.message == null){
            return;
        }
        if (this.context instanceof Guild){
            User user = ((Guild) this.context).liaison();
            if (user != null) {
                MessageHelper.send(user, "Regarding " + ((Guild) this.context).discord().getName() + ": " + this.message);
            }
        }else if (this.context instanceof User){
            MessageHelper.send(((User) this.context), this.message);
        }else if (this.override){
            MessageHelper.sendOverride(((Channel) this.context), this.message);
        }else{
            MessageHelper.send(((Channel) this.context), this.message);
        }
    }
    public boolean executed() {
        return this.executed;
    }
}
