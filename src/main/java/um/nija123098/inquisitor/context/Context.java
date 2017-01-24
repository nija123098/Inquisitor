package um.nija123098.inquisitor.context;

import um.nija123098.inquisitor.saving.Entity;
import um.nija123098.inquisitor.saving.Unique;
import um.nija123098.inquisitor.util.FileHelper;

import java.util.UnknownFormatConversionException;

/**
 * Made by nija123098 on 11/5/2016
 */
public class Context implements Unique {
    private String id;
    private Entity entity;
    Context(String contextName, String id) {
        this.entity = Entity.getEntity(FileHelper.getJarContainer() + "\\" + contextName, id);
        this.id = id;
    }
    public String getData(String id) {
        return this.entity.getData(id);
    }
    public String getData(String id, String defaul) {
        return this.entity.getData(id, defaul);
    }
    public void putData(String id, String data) {
        this.entity.putData(id, data);
    }
    public void clearData(String id){
        this.entity.clearData(id);
    }
    public String getID(){
        return this.id;
    }
}
