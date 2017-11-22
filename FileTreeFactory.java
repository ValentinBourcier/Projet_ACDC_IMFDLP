import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author valentin
 */
public class FileTreeFactory implements FileVisitor<Path>, Callable{
    
    public static DefaultMutableTreeNode root;
    private DefaultMutableTreeNode currentNode;
    private Filter filter;
    private final Path rootPath;
    private CacheManager cache;
    private Boolean hash;
    public Boolean recordInCache = true;
    
    public FileTreeFactory(Path rootPath, DefaultMutableTreeNode root, Filter filter, Boolean hash, Boolean recordInCache) {
        this.filter = filter;
        this.root = root;
        this.currentNode = this.root;
        this.rootPath = rootPath;
        this.cache = CacheManager.getInstance();
        this.hash = hash;
        this.recordInCache = recordInCache;
    }
    
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(dir)){
            if (root == null) {
                root = new DefaultMutableTreeNode(new FileNode(dir.toString()));
                currentNode = root;
            } else {
                DefaultMutableTreeNode directory = new DefaultMutableTreeNode(new FileNode(dir.toString()));
                currentNode.add(directory);
                currentNode = directory;
            }
        }
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        currentNode = (DefaultMutableTreeNode) currentNode.getParent();
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(path)){
            if(attrs.isRegularFile()){
                File file = new File(path.toString());
                boolean isValid = filter.isActive() ? filter.accept(file) : true;
                if(isValid){
                    DefaultMutableTreeNode node;
                    if(recordInCache && cache.contains(path.toString())){
                        node = new DefaultMutableTreeNode(cache.getMoreRecent(path.toString()));
                    }else{
                        FileNode fileNode = new FileNode(path.toString());
                        if(hash){
                            fileNode.hash("MD5");
                        }
                        node = new DefaultMutableTreeNode(fileNode);
                        if (recordInCache && !cache.contains(path.toString())) {
                            cache.add(fileNode);
                        }
                    }
                    currentNode.add(node);
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
    public DefaultMutableTreeNode call() throws Exception {
        try{
            Files.walkFileTree(rootPath, this);
        }catch(IOException error){
            error.printStackTrace();
            System.out.println("Error while creating FileTree");
        }
        return this.root;
    }
    
}