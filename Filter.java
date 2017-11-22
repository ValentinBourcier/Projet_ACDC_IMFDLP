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
     * @return True if the file is valid false ether.
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
     * Permet d'ajouter une contrainte de correspondance avec le nom de fichier
     * @param comparison Chaine qui doit etre contenu dans le nom de fichier
     */
    public void nameContains(String comparison){
        this.name = comparison;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec le poids du fichier
     * @param weight Poids qui doit etre égal à celui du fichier
     */
    public void weightEq(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = false;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec le poids du fichier
     * @param weight Poids qui doit etre inférieur à celui le fichier
     */
    public void weightGt(long weight){
        this.weight = weight;
        this.weightGt = true;
        this.weightLw = false;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec le poids du fichier
     * @param weight Poids qui doit etre supérieur à celui le fichier
     */
    public void weightLw(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = true;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec l'extension du fichier
     * @param extension Nouvelle extension qui doit etre acceptée par le filtre
     */
    public void acceptExtension(String extension){
        this.extensions.add(extension);
    }
    
    /**
     * Correspondance avec la date de modification du fichier
     * @param date Dernière date de modification du fichier
     */
    public void dateEq(Date date){
        this.date = date.getTime();
        this.dateGt = false;
        this.dateLw = false;
    }
    
    /**
     * Correspondance avec la date de modification du fichier
     * @param date Dernière date de modification du fichier
     */
    public void dateGt(Date date){
        this.date = date.getTime();
        this.dateGt = true;
        this.dateLw = false;
    }
    
    /**
     * Correspondance avec la date de modification du fichier
     * @param date Dernière date de modification du fichier
     */
    
    public void dateLw(Date date){
        this.date = date.getTime();
        this.dateGt = false;
        this.dateLw = true;
    }
    
    /**
     * Indique si le filtre doit valider ou non les dossiers
     * @param accept True si il faut effectuer des vérifications sur les dossiers false sinon.
     */
    public void acceptDirectory(boolean accept){
        this.directory = accept;
    }
    
    /**
     * Indique si le filtre est actif ou non
     * @return True si le filtre est actif, false sinon
     */
    public boolean isActive() {
        return this.extensions.isEmpty() == false || this.name != null || this.directory == false || this.date != 0 || this.weight != 0;
    }
    
}
