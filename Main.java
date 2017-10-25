
import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    public static void main(String[] args) {
        
        FileTreeFactory factory = new FileTreeConcreteFactory();
        FileTree tree = factory.createFileTree("/home/");
        System.out.println(tree.getDepth());
        System.exit(0);
//        File home = new File("/home/");
//        File[] files = home.listFiles(filter);
//        for (File file : files) {
//            System.out.println(file.getName());
//        }
    }
}
