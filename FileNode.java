
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
 *
 * @author valentin
 */
public class FileNode extends File{
    
        private String hash;
        
        public FileNode(File file) {
            super(file.getAbsolutePath());
            this.hash = hash("MD5");
            System.out.println(file.getAbsolutePath());
            
        }
        
        public FileNode(String path) {
            super(path);
        }
        
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
        
        public String getHash(){
            return this.hash;
        }

        @Override
        public String toString() {
            String name = this.getName();
            if (name.equals("")) {
                return this.getAbsolutePath();
            } else {
                return name;
            }
        }
        
        public Date getModificationDate(){
            return new Date(this.lastModified());
        }
        
}
