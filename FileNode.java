
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.xml.crypto.dsig.DigestMethod;

/**
 * Classe qui représente un noeud de l'arbre
 * 
 * @author valentin
 */
public class FileNode{
    
    private File value;
    private String hash;
    
    public FileNode(String path){
        this.value = new File(path);
        this.hash = null;
    }
    
    
    public boolean isDirectory() {
        return false;
    }

    
    public boolean isFile() {
        return true;
    }

    
    public long getWeight() {
        return this.value.length();
    }

    
    public File getValue() {
        return this.value;
    }
    
    
    public String hash(String algorithme){
        if(this.value.isFile()){
            DigestInputStream digest = null;
            try{
                MessageDigest md = MessageDigest.getInstance(algorithme);
                digest = new DigestInputStream(new FileInputStream(this.value.getAbsolutePath()), md);

                byte[] byteArray = new byte[8192];
                int bytesCount = 0;

                while (digest.read(byteArray) > 0){
                    md.update(byteArray, 0, bytesCount);
                }
                digest.close();

                byte[] bytes = md.digest();

                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++)
                {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }

                this.hash = sb.toString();

            }catch(NoSuchAlgorithmException error){
                System.out.println("Hashage impossible, algorithme non supporté.");
            }catch(FileNotFoundException error){
                error.printStackTrace();
                System.out.println("Hashage impossible, fichier non trouvé.");
            }catch(IOException error){
                System.out.println("Hashage interrompu, erreur de lecture.");
            }

            return this.hash;
        }
        return null;
    }
    
    public String getHash(){
        if(this.hash == null){
            hash("MD5");
        }
        return this.hash;
    }
    
    public String toString(){
        return this.value.getName();
    }

}
