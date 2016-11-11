package um.nija123098.inquisitor.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 11/8/2016
 */
public class Entity {
    private final Map<String, String> stringMap;
    private final String name;
    public Entity(String name) {
        this.stringMap = new ConcurrentHashMap<String, String>();
        this.name = name;
    }
    public Entity(String name, List<String> strings) {
        this(name);
        for (String s : strings) {
            String[] st = s.split(":");
            this.stringMap.put(st[0], s.substring(st[0].length() + 1));
        }
    }
    public String getData(String id) {
        return this.stringMap.get(id);
    }
    public void putData(String id, String data) {
        this.stringMap.put(id, data);
    }
    public void clearData(String id){
        this.stringMap.remove(id);
    }
    public List<String> getStrings() {
        List<String> strings = new ArrayList<String>(this.stringMap.size());
        strings.addAll(this.stringMap.keySet().stream().map(key -> key + ":" + this.stringMap.get(key)).collect(Collectors.toList()));
        return strings;
    }
    public String name(){
        return this.name;
    }
}
