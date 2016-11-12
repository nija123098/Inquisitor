package um.nija123098.inquisitor.bot;

import um.nija123098.inquisitor.util.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Made by nija123098 on 11/12/2016
 */
public class Entity {
    private static final List<Entity> ENTITIES;
    static {
        ENTITIES = new ArrayList<Entity>();
    }
    public static void saveEntities(){
        ENTITIES.forEach(Entity::save);
    }
    public static Entity getEnt(String path, String name){
        File file = new File(path + "\\" + name);
        for (Entity entity : ENTITIES) {
            if (entity.file.equals(file)){
                return entity;
            }
        }
        Entity entity = new Entity(file);
        ENTITIES.add(entity);
        return entity;
    }
    private final Map<String, String> stringMap;
    private final File file;
    public Entity(String path, String name) {
        this(new File(path + "\\" + name));
    }
    public Entity(File file) {
        this.stringMap = new ConcurrentHashMap<String, String>();
        this.file = file;
        ENTITIES.add(this);
        List<String> strings = new ArrayList<String>();
        try{
            strings = Files.readAllLines(Paths.get(this.file.getPath()));
            if (strings == null){
                strings = new ArrayList<String>(0);
            }
        }catch(Exception ignored){}
        for (String s : strings) {
            String[] st = s.split(":");
            this.stringMap.put(st[0], s.substring(st[0].length() + 1));
        }
    }
    public String getData(String id) {
        return this.stringMap.get(id);
    }
    public String getData(String id, String defaul) {
        String data = this.stringMap.get(id);
        if (data == null){
            data = defaul;
        }
        return data;
    }
    public void putData(String id, String data) {
        this.stringMap.put(id, data);
    }
    public void clearData(String id){
        this.stringMap.remove(id);
    }
    private List<String> getStrings() {
        List<String> strings = new ArrayList<String>(this.stringMap.size());
        strings.addAll(this.stringMap.keySet().stream().map(key -> key + ":" + this.stringMap.get(key)).collect(Collectors.toList()));
        return strings;
    }
    public String name(){
        return this.file.getName();
    }
    public void save(){
        if (this.stringMap.size() > 0){
            try{
                if (!Files.exists(Paths.get(this.file.getParent()))){
                    Files.createDirectory(Paths.get(this.file.getParent()));
                }
                if (!Files.exists(Paths.get(this.file.getPath()))){
                    Files.createFile(Paths.get(this.file.getPath()));
                }
                // Files.write(Paths.get(this.file.getPath()), this.getStrings());
            }catch(Exception e){Log.error("Can not save " + this.name() + " because of " + e.getMessage());
            e.printStackTrace();}
        }
    }
}
