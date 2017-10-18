
import java.io.File;
import java.io.FileFilter;
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
        
        // Création d'un filtre de recherche
        Filter filter = new Filter();
        filter.acceptExtension("pdf");
        
        // Création d'une arborescence de fichiers
        FileTree tree = new FileTree("/home/", filter);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(tree);
        executor.shutdown();
        
        // Affichage de la profondeur de l'arbre (sleep utilisé car la mise en place du multi threading n'est pas complète)
        try{Thread.sleep(2000);}catch(Exception e){}
        System.out.println(tree.root().getDepth());
        
//        File home = new File("/home/");
//        File[] files = home.listFiles(filter);
//        for (File file : files) {
//            System.out.println(file.getName());
//        }
    }
}
