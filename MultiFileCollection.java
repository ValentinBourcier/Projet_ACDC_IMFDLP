
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valentin
 */
public class MultiFileCollection<String, FileNode> {
    
    private HashMap<String, Object> fileCollection;

    
   
    public synchronized void putFile(String hash, FileNode fileNode){
        
        Object file = (FileNode) fileCollection.get(hash);
        if (file == null){
            fileCollection.put(hash, fileNode);
        }else if(file instanceof ArrayList){
           ((ArrayList<FileNode>) file).add(fileNode);
        }
    }
    

    public Object get(String hash){
        return fileCollection.get(hash);
    }
    
    public ArrayList<FileNode> getDuplicates(){
        ArrayList<FileNode> duplicates = new ArrayList<>();
        for (Object item : fileCollection.values()) {
            if(item instanceof ArrayList){
                duplicates.addAll((ArrayList<FileNode>) item);
            }
        }
        return duplicates;
    }
    
    public HashMap<String, Object> getFileCollection() {
        return fileCollection;
    }
}
