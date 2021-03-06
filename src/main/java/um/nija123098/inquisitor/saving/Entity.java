package um.nija123098.inquisitor.saving;

import um.nija123098.inquisitor.util.FileHelper;
import um.nija123098.inquisitor.util.Log;

import java.io.File;
import java.io.IOException;
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
    private static final String CONTAINER;
    static{
        CONTAINER = new File(Entity.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent() + "\\";
        ENTITIES = new ArrayList<>();
    }
    public static synchronized void saveEntities(){
        ENTITIES.forEach(Entity::save);
    }
    public static synchronized Entity getEntity(String path, String name){
        File file = new File(FileHelper.getJarContainer() + "\\" + path + "\\" + name);
        for (Entity ENTITY : ENTITIES) {
            if (ENTITY.file.equals(file)) {
                return ENTITY;
            }
        }
        return new Entity(file);
    }
    public static synchronized List<Entity> getEntities(String path){
        List<Entity> entities = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files != null){
            for (File file : files) {
                entities.add(new Entity(file));
            }
        }
        return entities;
    }
    private final Map<String, String> stringMap;
    private final File file;
    private Entity(File file) {
        this.stringMap = new ConcurrentHashMap<>();
        this.file = file;
        List<String> strings = new ArrayList<>();
        try{strings = Files.readAllLines(Paths.get(this.file.getPath()));
        }catch(Exception ignored){}
        if (strings == null){
            strings = new ArrayList<>(0);
        }
        strings.stream().filter(s -> s.length() > 2).forEach(str -> {
            String[] st = str.split(":");
            this.stringMap.put(st[0], str.substring(st[0].length() + 1));
        });
        ENTITIES.add(this);
    }
    public String getData(String id) {
        return this.stringMap.get(id);
    }
    public String getData(Unique unique){
        return getData(unique.getID());
    }
    public String getData(String id, String defaul) {
        String data = this.stringMap.get(id);
        if (data == null){
            data = defaul;
        }
        return data;
    }
    public String getData(Unique unique, String defaul){
        return getData(unique.getID(), defaul);
    }
    public void putData(String id, String data) {
        this.stringMap.put(id, data);
    }
    public void putData(Unique unique, String data) {
        this.stringMap.put(unique.getID(), data);
    }
    public void clearData(String id){
        this.stringMap.remove(id);
    }
    public void clearData(Unique unique){
        this.stringMap.remove(unique.getID());
    }
    public List<String> getSaved(){
        return new ArrayList<>(this.stringMap.keySet());
    }
    public List<String> getValues(){
        return new ArrayList<>(this.stringMap.values());
    }
    private List<String> getStrings() {
        List<String> strings = new ArrayList<>(this.stringMap.size());
        strings.addAll(this.stringMap.keySet().stream().map(key -> key + ":" + this.stringMap.get(key)).collect(Collectors.toList()));
        return strings;
    }
    public void clearData(){
        this.stringMap.clear();
    }
    public String name(){
        return this.file.getName();
    }
    public synchronized void save(){
        if (this.stringMap.size() > 0){
            try{
                if (!Files.exists(Paths.get(this.file.getParent()))){
                    Files.createDirectory(Paths.get(this.file.getParent()));
                }
                if (!Files.exists(Paths.get(this.file.getPath()))){
                    Files.createFile(Paths.get(this.file.getPath()));
                }
                Files.write(Paths.get(this.file.getPath()), this.getStrings());
            }catch(Exception e){Log.error("Can not save " + this.name() + " because of " + e.getMessage());
            e.printStackTrace();}
        }else{
            try{Files.deleteIfExists(Paths.get(this.file.getPath()));
            }catch(IOException e){Log.error("Can not save " + this.name() + " because of " + e.getMessage());
            e.printStackTrace();}
        }
    }
}
