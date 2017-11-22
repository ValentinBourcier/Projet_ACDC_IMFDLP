import java.io.File;
import java.util.List;
import java.util.Map;
/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    
    public static Analyzer analyzer = new FileTree();
    
    public static void testBuildFileTree(){
        // Build the file tree without hashing or storing files.
        analyzer.buildFileTree("/home/", false, true);
        System.out.println(analyzer);
    }
    
    public static void testDuplicates(){
        Map<String, List<File>> dup = analyzer.getDuplicates("/home/valentin/Tests", new Filter());
        for (String hash : dup.keySet()) {
            for (File file: dup.get(hash)) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }
    
    public static void main(String[] args) {
        testBuildFileTree();
        testDuplicates();
        
        System.exit(0);    
    }
}
