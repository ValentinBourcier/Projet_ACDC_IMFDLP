package Analyzer.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class defining a file filter
 * 
 * @author valentin
 */
public class Filter implements FileFilter{
    
    private long weight;
    private boolean weightGt;
    private boolean weightLw;
    private ArrayList<String> extensions;
    private long date;
    private boolean dateGt;
    private boolean dateLw;
    private String name;
    private boolean directory;
    
    /**
     * Filter initialisation
     */
    public Filter(){
        this.extensions = new ArrayList<>();
        this.name = null;
        this.directory = true;
        this.date = 0;
        this.weight = 0;
    }
    
    /**
     * Method checking the correspondance of a file with the filter
     * @return True if the file is valid false either.
     */
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
        
        if(this.dateGt && this.date > 0){
            accept = (accept && file.lastModified() > date);
        }else if(this.dateLw && this.date > 0){
            accept = (accept && file.lastModified() < date);
        }else if(this.date > 0){
            accept = (accept && file.lastModified() == date);
        }
        
        if(!extensions.isEmpty() && !file.isDirectory()){
            String extension = "";
            int i = file.getName().lastIndexOf('.');
            if (i > 0) {
                extension = file.getName().substring(i+1);
            }
            accept = (extensions.contains(extension));
        }  
        if(this.name != null){
            accept = (accept && file.getName().contains(this.name));
        }
        return accept;
    }
    
    /**
     * Adding a file name constraint
     * @param comparison String which should be contained in file name
     */
    public void nameContains(String comparison){
        this.name = comparison;
    }
    
    /**
     * Adding a file weight equality constraint
     * @param weight Weight that the file should have
     */
    public void weightEq(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = false;
    }
    
    
    /**
     * Adding a file weight constraint (greater than)
     * @param weight Weight that the file should be greater than
     */
    public void weightGt(long weight){
        this.weight = weight;
        this.weightGt = true;
        this.weightLw = false;
    }
    
    
    /**
     * Adding a file weight constraint (lower than)
     * @param weight Weight that the file should be lower than
     */
    public void weightLw(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = true;
    }
    
    
    /**
     * Adding a valid extension
     * @param extension Extension to accept
     */
    public void acceptExtension(String extension){
        this.extensions.add(extension);
    }
    
    /**
     * Adding a date equality constraint
     * @param date Modification date of the file that should be verified
     */
    public void dateEq(Date date){
        this.date = date.getTime();
        this.dateGt = false;
        this.dateLw = false;
    }
    
    /**
     * Adding a date constraint (Older than)
     * @param date Modification date of the file that should be verified
     */
    public void dateGt(Date date){
        this.date = date.getTime();
        this.dateGt = true;
        this.dateLw = false;
    }
    
    /**
     * Adding a date constraint (Less older than)
     * @param date Modification date of the file that should be verified
     */
    public void dateLw(Date date){
        this.date = date.getTime();
        this.dateGt = false;
        this.dateLw = true;
    }
    
    /**
     * Method to check directories
     * @param accept Boolean equals to true if we should check directories validity, false either.
     */
    public void acceptDirectory(boolean accept){
        this.directory = accept;
    }
    
    /**
     * Method saying if filter was activate
     * @return True is filter was activated, false either
     */
    public boolean isActive() {
        return this.extensions.isEmpty() == false || this.name != null || this.directory == false || this.date != 0 || this.weight != 0;
    }
    
}
