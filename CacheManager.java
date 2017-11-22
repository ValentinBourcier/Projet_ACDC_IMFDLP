import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author valentin
 */
public class CacheManager {
    
    private HashMap<String, FileNode> cache = new HashMap<>();
    private String location;
    private Boolean modified;
    private static CacheManager manager;

    private CacheManager(){
        cache = new HashMap<>();
        location = "tree.cache";
        modified = false;
    }
        
    public static synchronized CacheManager getInstance(){
        if(manager == null){
            manager = new CacheManager();
        }
        return manager;
    }
    
    public void add(String path, FileNode file){
        this.cache.put(path, file);
        this.modified = true;
    }
    
    public void add(FileNode file){
        this.cache.put(file.getAbsolutePath(), file);
        this.modified = true;
    }
    
    public void remove(String path){
        this.cache.remove(path);
        this.modified = true;
    }
    
    public FileNode get(String path){
        if(isSet()){
            return this.cache.get(path);
        }
        throw new UnsupportedOperationException("Cache is not Set.");
    }
    
    public boolean isSet(){
        return Files.exists(Paths.get(this.location));
    }
    
    public boolean contains(String path){
        if(isSet()){
            return this.cache.containsKey(path);
        }
        return false;
    }
    
    public FileNode getMoreRecent(String path){
        FileNode cacheNode = get(path);
        FileNode systemNode = new FileNode(path);
        return cacheNode.INSTANCE_TIME > systemNode.lastModified() ? cacheNode : systemNode; 
    }
    
    public void update(String path){
        this.cache.put(path, getMoreRecent(path));
    }
    
    public void serialize(){
        try{
            FileOutputStream file = new FileOutputStream(this.location, false);
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(cache);
            stream.close();
            file.close();
        }catch(IOException error){
            error.printStackTrace();
        }
    }
    
    public void unserialize(){
        if(isSet() && modified){
            try
            {
                FileInputStream file = new FileInputStream(this.location);
                ObjectInputStream stream = new ObjectInputStream(file);
                cache = (HashMap) stream.readObject();
                stream.close();
                file.close();
            }catch(IOException error){
                error.printStackTrace();
            }catch(ClassNotFoundException error){
                error.printStackTrace();
            }
        }
    }
    
    public void clean(){
        if(isSet()){
            new File(this.location).delete();
        }
    }
    
}
