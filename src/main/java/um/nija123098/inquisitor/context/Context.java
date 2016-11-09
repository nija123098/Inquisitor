package um.nija123098.inquisitor.context;

import um.nija123098.inquisitor.bot.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Context extends Entity {
    private String id;
    public Context(String id) {
        this(id, new ArrayList<String>());
    }
    public Context(String id, List<String> strings) {
        super(id, strings);
        this.id = id;
    }
    public String getID(){
        return this.id;
    }
}
