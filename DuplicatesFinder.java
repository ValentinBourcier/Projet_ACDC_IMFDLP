
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.tree.DefaultMutableTreeNode;

public class DuplicatesFinder implements FileVisitor<Path>, Callable{
    
    private static ConcurrentHashMap<String, Object> duplicates;
    private Filter filter;
    private final Path rootPath;

    private static class ExecutorManager
    {		
        public static int MAX_THREADS = 3;
        public static int NB_THREADS = 0;
        private static ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static ThreadPoolExecutor getExecutorInstance()
    {
        return ExecutorManager.EXECUTOR;
    }
    
    public DuplicatesFinder(Path rootPath, Filter filter) {
        this.filter = filter;
        this.rootPath = rootPath;
        this.duplicates = new ConcurrentHashMap<String, Object>();
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(dir)){
            getExecutorInstance().submit(new DuplicatesFinder(dir, filter));
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
            FileNode element = new FileNode(path.toString());
            if(element.getValue().isFile()){
                String hash = element.getHash();
                Object file = (Object) this.duplicates.get(hash);
                if (file == null){
                    this.duplicates.put(hash, element);
                }else if(file instanceof ArrayList){
                   ((ArrayList) file).add(element);
                }else{
                    ArrayList<Object> duplicated = new ArrayList<>();
                    duplicated.add((Object) element);
                    duplicated.add(file);
                    this.duplicates.put(hash, duplicated);
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
    public DuplicatesFinder call() throws Exception {
        try{
            Files.walkFileTree(rootPath, this);
        }catch(IOException error){
            System.out.println("Error while parsing files");
            return null;
        }
        return this;
    }
    
    public static DuplicatesFinder searchDuplicates(String rootPath, Filter filter){
        Path path = Paths.get(rootPath);
        DuplicatesFinder finder = new DuplicatesFinder(path, filter);
        getExecutorInstance().submit(finder);
        return finder;
    }
    
    public static ArrayList<FileNode> getDuplicates(String rootPath){
        ArrayList<FileNode> duplicated = new ArrayList<>();
        for (Object item : duplicates.values()) {
            if(item instanceof ArrayList){
                duplicated.addAll((ArrayList<FileNode>) item);
            }
        }
        return duplicated;
    }
    
}