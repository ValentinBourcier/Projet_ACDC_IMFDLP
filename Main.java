
import java.io.File;
import java.io.FileFilter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author valentin
 */
public class Main {
    public static void main(String[] args) {
        Filter filter = new Filter();
        filter.acceptExtension("pdf");
        FileTree tree = new FileTree("/home/", filter);
        
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(tree);
        executor.shutdown();
        try{Thread.sleep(2000);}catch(Exception e){}
        System.out.println(tree.root().getDepth());
        
//        File home = new File("/home/");
//        File[] files = home.listFiles(filter);
//        for (File file : files) {
//            System.out.println(file.getName());
//        }
    }
}
