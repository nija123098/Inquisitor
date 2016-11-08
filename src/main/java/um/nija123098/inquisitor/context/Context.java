package um.nija123098.inquisitor.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Context {
    private Map<String, String> stringMap;
    private String id;
    public Context(String id){
        this(id, new ArrayList<String>());
    }
    public Context(String id, List<String> strings){
        this.id = id;
        for (String s : strings) {
            String[] st = s.split(":");
            this.stringMap.put(st[0], s.substring(st[0].length()));
        }
    }
    public String getID(){
        return this.id;
    }
    public String getData(String id){
        return this.stringMap.get(id);
    }
    public void putData(String id, String data){
        this.stringMap.put(id, data);
    }

    public List<String> getStrings() {
        List<String> strings = new ArrayList<String>(this.stringMap.size());
        strings.addAll(this.stringMap.keySet().stream().map(key -> key + ":" + this.stringMap.get(key)).collect(Collectors.toList()));
        return strings;
    }
}
