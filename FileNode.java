import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Class defining the FileNode object in FileTree
 * 
 * @author Valentin Bourcier
 */
public class FileNode extends File implements Serializable{
    
    private String hash;
    // Date of creation of the current FileNode
    public final long INSTANCE_TIME = System.currentTimeMillis();
    
    /**
     * FileNode builder
     */
    public FileNode(String path){
        super(path);
        this.hash = null;
    }
    
    /**
     * Method calculating hash of a file
     * @param algorithme Digest algorithme used: "MD5", "SHA1"...
     * @return The hash string of current file
     */
    public String hash(String algorithme){
        if(this.isFile() && this.canRead()){
            DigestInputStream digest = null;
            try{
                MessageDigest md = MessageDigest.getInstance(algorithme);
                digest = new DigestInputStream(new FileInputStream(this.getAbsolutePath()), md);

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
    
    /**
     * Method launching hash if not already calculated
     * @return The hash string of current file 
     */
    public String getHash(){
        if(this.hash == null){
            hash("MD5");
        }
        return this.hash;
    }
    
    /**
     * String representation of the file
     */
    public String toString(){
        return this.getName();
    }

}
