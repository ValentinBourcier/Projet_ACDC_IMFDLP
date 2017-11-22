import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author valentin
 */
public class DuplicatesFinder implements FileVisitor<Path>, Callable{
    
    private Map<String, List<File>> duplicates;
    private Filter filter;
    private final Path rootPath;
    private CacheManager cache = CacheManager.getInstance();
    private static ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    

    public DuplicatesFinder(Path rootPath, Filter filter) {
        this.filter = filter;
        this.rootPath = rootPath;
        this.duplicates = new HashMap<String, List<File>>();
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(!Files.isReadable(dir)){
            System.out.println("Erreur d'accès au dossier: " + dir.toString());
        }
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(path)){
            if(filter.accept(path.toFile())){
                FileNode element = new FileNode(path.toString());
                if(!cache.contains(element.getAbsolutePath())){
                    cache.add(element);
                }else{
                    System.out.println("test");
                    element = cache.getMoreRecent(element.getAbsolutePath());
                }
                String hash = element.getHash();
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
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("File reading failed for: " + exc.getMessage());
        return FileVisitResult.CONTINUE;
    }
    
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
    
    public static Map<String, List<File>> searchDuplicates(String rootPath, Filter filter){
        Path path = Paths.get(rootPath);
        DuplicatesFinder finder = new DuplicatesFinder(path, filter);
        Future<Map<String, List<File>>> duplicated = EXECUTOR.submit(finder);
        try{
            return duplicated.get();
        }catch(Exception error){
            return null;
        }
    }
    
}
