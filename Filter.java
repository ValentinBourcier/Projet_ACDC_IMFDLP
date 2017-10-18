
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author valentin
 */
public class Filter implements FileFilter{
    
    private long weight;
    private boolean weightGt;
    private boolean weightLw;
    private ArrayList<String> extensions;
    private Date modification;
    private String name;
    private boolean directory;
    
    public Filter(){
        this.extensions = new ArrayList<>();
        this.modification = null;
        this.name = null;
        this.directory = true;
    }
    
    @Override
    public boolean accept(File file) {
        boolean accept = true;
        
        if(file.isDirectory()){
            accept = (accept && directory);
        }
        
        if(this.weightGt && this.weight > 0){
            accept = (accept && file.length() > weight);
        }else if(this.weightLw && this.weight > 0){
            accept = (accept && file.length() < weight);
        }else if(this.weight > 0){
            accept = (accept && file.length() == weight);
        }
        
        if(!extensions.isEmpty()){
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);
            }
            accept = (extensions.contains(extension));
        }
        if(this.modification != null){
            accept = (accept && this.modification == new Date(file.lastModified()));
        }
        if(this.name != null){
            accept = (accept && file.getName().contains(this.name));
        }
        return accept;
    }
    
    public void nameContains(String comparison){
        this.name = comparison;
    }
    
    public void weightEquals(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = false;
    }
    
    public void weightGt(long weight){
        this.weight = weight;
        this.weightGt = true;
        this.weightLw = false;
    }
    
    public void weightLw(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = true;
    }
    
    public void acceptExtension(String extension){
        this.extensions.add(extension);
    }
    
    public void modification(Date date){
        this.modification = date;
    }
    
    public void acceptDirectory(boolean accept){
        this.directory = accept;
    }
    
}
