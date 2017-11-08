
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author valentin
 */
public class FileTreeFactory implements FileVisitor<Path>, Callable{
    
    public FileTree tree;
    private DefaultMutableTreeNode currentNode;
    private Filter filter;
    private final Path rootPath;
    private List<Future<FileTree>> results;
     
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
    
    public FileTreeFactory(Path rootPath, Filter filter) {
        this.filter = filter;
        this.tree = null;
        this.rootPath = rootPath;
        this.results = new ArrayList<>();
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(dir)){
            
            if (!dir.equals(FileTreeFactory.this.rootPath)) {
                FileTreeFactory factory = new FileTreeFactory(dir, filter);
                Future<FileTree> result = getExecutorInstance().submit(factory);
                results.add(result);
                return FileVisitResult.SKIP_SUBTREE;
            }else{
                File directory = dir.getFileName().toFile();
                tree = new FileTree(directory.getAbsolutePath());
                currentNode = this.tree.getRoot();
            }
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
            if(attrs.isRegularFile()){
                File file = new File(path.toString());
                boolean isValid = filter.isActive() ? filter.accept(file) : true;
                if(isValid){
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileNode(path.toString()));
                    currentNode.add(node);
                    currentNode = node;
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
    public FileTree call() throws Exception {
        try{
            Files.walkFileTree(rootPath, this);
        }catch(IOException error){
            System.out.println("Error while creating FileTree");
            return null;
        }
        for (Future<FileTree> result : results) {
            this.tree.getRoot().add(result.get().getRoot());
        }
        return this.tree;
    }
    
    public static FileTree createFileTree(String rootPath, Filter filter){
        Path path = Paths.get(rootPath);
        FileTreeFactory factory = new FileTreeFactory(path, filter);
        Future<FileTree> result = getExecutorInstance().submit(factory);
        FileTree tree = null;
        try{
            tree = result.get();     
        }catch(Exception error){
            System.out.println("FileTree building failed");
        }
        if(filter.isActive()){
            tree.cleanFolders();
        }
        return tree;
    }
    
    public static FileTree createFileTree(String rootPath){
        return createFileTree(rootPath, new Filter());
    }
    
}
