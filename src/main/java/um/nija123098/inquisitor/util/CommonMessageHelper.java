package um.nija123098.inquisitor.util;

import um.nija123098.inquisitor.command.Command;
import um.nija123098.inquisitor.context.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/10/2016
 */
public class CommonMessageHelper {
    public static void displayHelp(String pre, String post, List<Command> commandList, User user){
        List<String> one = new ArrayList<String>(commandList.size()), two = new ArrayList<String>(commandList.size());
        commandList.forEach(command -> {
            one.add(command.name());
            two.add(command.help());
        });
        displayLists(pre, post, one, two, user);
    }
    public static void displayLists(String pre, String post, List<String> one, List<String> two, User user){
        String string = pre + "\n#========================\n";
        for (int j = 0; j < one.size() || j < two.size(); j++) {
            string += "[" + (j < one.size() ? one.get(j) : "") + "](" + (j < two.size() ? two.get(j) : "") + ")\n";
        }
        string += "#========================\n";
        MessageHelper.send(user, post + "```md\n" + string + "```");
    }
    public static void displayList(String pre, String post, List<String> one, User user){
        displayLists(pre, post, one, new ArrayList<String>(0), user);
    }
}
