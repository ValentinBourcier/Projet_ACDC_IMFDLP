
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe définissant un filtre de recherche
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
    
    /**
     * Initialisation du filtre
     */
    public Filter(){
        this.extensions = new ArrayList<>();
        this.modification = null;
        this.name = null;
        this.directory = true;
    }
    
    /**
     * Méthode qui permet de valider ou non la correspondance d'un fichier avec le filtre
     * @return True si le fichier est valide, false sinon.
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
    public void weightEquals(long weight){
        this.weight = weight;
        this.weightGt = false;
        this.weightLw = false;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec le poids du fichier
     * @param weight Poids qui doit etre supérieur à celui le fichier
     */
    public void weightGt(long weight){
        this.weight = weight;
        this.weightGt = true;
        this.weightLw = false;
    }
    
    /**
     * Permet d'ajouter une contrainte de correspondance avec le poids du fichier
     * @param weight Poids qui doit etre inférieur à celui le fichier
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
    public void modification(Date date){
        this.modification = date;
    }
    
    /**
     * Indique si le filtre doit accepter d'explorer les sous-dossiers ou non
     * @param accept True si il faut parcourir les sous dossiers false sinon.
     */
    public void acceptDirectory(boolean accept){
        this.directory = accept;
    }
    
}
