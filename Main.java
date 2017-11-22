import java.io.File;
import java.util.List;
import java.util.Map;
/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    public static void main(String[] args) {
        Analyzer analyzer = new FileTree();
        analyzer.buildFileTree("/home/valentin/", false, true);
        Map<String, List<File>> dup = analyzer.getDuplicates("/home/valentin/Tests", new Filter());
        for (String hash : dup.keySet()) {
            for (File file: dup.get(hash)) {
                System.out.println(file.getAbsolutePath());
            }
        }
        System.exit(0);
        
    }
}
