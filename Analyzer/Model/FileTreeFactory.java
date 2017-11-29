package Analyzer.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import javax.swing.tree.DefaultMutableTreeNode;

import Analyzer.Control.CacheManager;
import Analyzer.Service.Filter;

/**
 * Class building a file tree
 * 
 * @author Valentin Bourcier
 */
public class FileTreeFactory implements FileVisitor<Path>, Callable{
    
    public static DefaultMutableTreeNode root;
    private DefaultMutableTreeNode currentNode;
    private Filter filter;
    private final Path rootPath;
    private CacheManager cache;
    private Boolean hash;
    private Boolean recordInCache = true;
    private int maxDepth;
    
    /**
     * Factory initializer
     * @param rootPath Path of the root file
     * @param 
     */
    public FileTreeFactory(Path rootPath, DefaultMutableTreeNode root, Filter filter, Boolean hash, Boolean recordInCache, int maxDepth) {
        this.filter = filter;
        this.root = root;
        this.currentNode = this.root;
        this.rootPath = rootPath;
        this.cache = CacheManager.getInstance();
        this.hash = hash;
        this.recordInCache = recordInCache;
        this.maxDepth = maxDepth;
    }
    
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(Files.isReadable(dir)){
            Path file = ((FileNode) this.root.getUserObject()).toPath();
            if(!file.equals(dir)){
                DefaultMutableTreeNode directory = new DefaultMutableTreeNode(new FileNode(dir.toString()));
                currentNode.add(directory);
                currentNode = directory;
            }
        }
        return FileVisitResult.CONTINUE;
    }
 
    /**
     * Inherited from FileVisitor
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        currentNode = (DefaultMutableTreeNode) currentNode.getParent();
        return FileVisitResult.CONTINUE;
    }
 
    /**
     * Inherited from FileVisitor
     */
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
    public DefaultMutableTreeNode call() throws Exception {
        try{
            if(maxDepth > 0){
                Files.walkFileTree(rootPath, EnumSet.noneOf(FileVisitOption.class), this.maxDepth, this);
            }else{
                Files.walkFileTree(rootPath, this);
            }
            
        }catch(IOException error){
            error.printStackTrace();
            System.out.println("Error while creating FileTree");
        }
        return this.root;
    }
    
}