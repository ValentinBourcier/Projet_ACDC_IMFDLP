import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class collecting duplicates files
 * 
 * @author Valentin Bourcier
 */
public class DuplicatesFinder implements FileVisitor<Path>, Callable{
    
    private Map<String, List<File>> duplicates;
    private Filter filter;
    private final Path rootPath;
    private CacheManager cache = CacheManager.getInstance();
    private static ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    

    /**
     * Builder of the finder
     * @param rootPath Path of the root file
     * @param filter Filter instance checking duplicates files validity
     */
    public DuplicatesFinder(Path rootPath, Filter filter) {
        this.filter = filter;
        this.rootPath = rootPath;
        this.duplicates = new HashMap<String, List<File>>();
    }
    
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(!Files.isReadable(dir)){
            System.out.println("Erreur d'accès au dossier: " + dir.toString());
        }
        return FileVisitResult.CONTINUE;
    }
 
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
 
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(path)){
            if(filter.accept(path.toFile())){
                FileNode element = new FileNode(path.toString());
                if(!cache.contains(element.getAbsolutePath())){
                    cache.add(element);
                }else{
                    element = cache.getMoreRecent(element.getAbsolutePath());
                }
                String hash = element.getHash();
                if(hash != null){
                        List<File> files = duplicates.get(hash);
                    if(files != null){
                        files.add(element);
                    }else{
                        ArrayList<File> list = new ArrayList<>();
                        list.add(element);
                        duplicates.put(hash, list);
                    }
                }
                
            }
        }
        return FileVisitResult.CONTINUE;
    }
 
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("File reading failed for: " + exc.getMessage());
        return FileVisitResult.CONTINUE;
    }
    
    /**
     * Method launching the research
     */
    @Override
    public Map<String, List<File>> call() throws Exception {
        try{
            Files.walkFileTree(rootPath, this);
        }catch(IOException error){
            System.out.println("Error while parsing files");
            return null;
        }
        for (String hash : duplicates.keySet()) {
            if(duplicates.get(hash).size() <= 1){
                duplicates.remove(hash);
            }
        }
        return duplicates;
    }
    
}
