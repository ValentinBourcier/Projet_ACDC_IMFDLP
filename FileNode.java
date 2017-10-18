
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.xml.crypto.dsig.DigestMethod;

/**
 * Classe qui représnte un noeud de l'arbre
 * 
 * @author valentin
 */
public class FileNode extends File{
    
    private String hash;

    /**
     * Création d'un noeud de l'arbre 
     * @param file Instance du fichier de référence
     */
    public FileNode(File file) {
        super(file.getAbsolutePath());
        this.hash = hash("MD5");
        System.out.println(file.getAbsolutePath());

    }

    /**
     * Création d'un noeud de l'arbre à partir de son chemin
     * @param path  Chemin du fichier représenté par le noeud
     */
    public FileNode(String path) {
        super(path);
    }
        
    /**
     * Méthode qui permet de "hasher" le fichier courant
     * @param algorithm Chaine de caractères représentant l'algorithme de hash à utiliser
     * @return La chaine de caractères correspondant à la somme de controle du fichier
     */
    public String hash(String algorithm)
    {   
        if(!this.isDirectory()){
            MessageDigest digest = null;
            try{
                digest = MessageDigest.getInstance("MD5");
                FileInputStream fis = new FileInputStream(this);

                byte[] byteArray = new byte[8024];
                int bytesCount = 0;

                while ((bytesCount = fis.read(byteArray)) != -1) {
                    digest.update(byteArray, 0, bytesCount);
                };

                fis.close();
            }catch(NoSuchAlgorithmException error){
                System.out.println("Hashage impossible, algorithme non supporté.");
            }catch(FileNotFoundException error){
                System.out.println("Hashage impossible, fichier non trouvé.");
            }catch(IOException error){
                System.out.println("Hashage interrompu, erreur de lecture.");
            }

            byte[] bytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Méthode qui retourne le hash du fichier
     * @return Le hash du fichier
     */
    public String getHash(){
        return this.hash;
    }

    /**
     * Représentation textuelle du fichier
     * @return La chaine de caractère du nom et chemin du fichier
     */
    @Override
    public String toString() {
        String name = this.getName();
        if (name.equals("")) {
            return this.getAbsolutePath();
        } else {
            return name;
        }
    }

    /**
     * Méthode qui permet de récupérer la date de modification du fichier
     * @return La Date de la plus récente modification
     */
    public Date getModificationDate(){
        return new Date(this.lastModified());
    }

}
