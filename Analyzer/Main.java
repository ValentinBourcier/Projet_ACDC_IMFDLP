package Analyzer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    
    public static Analyzer analyzer = new FileTree();
    
    /**
     * Build the file tree without hashing or storing files. 
     */
    public static void testBuildFileTree(){
        System.out.println("Building tree: ");
        analyzer.buildFileTree("src/tests", false, true, 0);
        System.out.println(analyzer);
    }
    
    /**
     * Collecting duplicates without filtering it
     */
    public static void testDuplicates(){
        System.out.println("Searching duplicates: ");
        Map<String, List<File>> dup = analyzer.getDuplicates("src/tests", new Filter());
        for (String hash : dup.keySet()) {
            for (File file: dup.get(hash)) {
                System.out.println(hash + " -> " + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static void testCache(){
        
        System.out.println("Deleting cache if exists");
        analyzer.cleanCache();
        
        // Building a FileTree in order to put some files in cache
        System.out.println("Filling cache");
        analyzer.buildFileTree("src/tests", false, true, 0);
        
        System.out.println("Cache is initialized: " + Files.exists(Paths.get(CacheManager.LOCATION)));
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static void testDeletion(){
        System.out.println("Testing deletion: ");
        analyzer.buildFileTree("src/tests", false, true, 0);
        analyzer.deleteNode("tests/folder1/duplicated-file1");
        System.out.println("File deleted: " + !Files.exists(Paths.get("src/tests/folder1/duplicated-file1")));
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static void testGetFileNodeAndWeight(){
        analyzer.buildFileTree("src/tests", false, true, 0);
        DefaultMutableTreeNode root = analyzer.getRoot();
        FileNode fileRoot = analyzer.getFileNode(root);
        System.out.println("FileNode == fileRoot: " + (fileRoot == ((FileNode) root.getUserObject())));
        System.out.println("Tree weight (20): " + analyzer.getWeight(root));
    }
    
    /**
     * Deleting empty directories from File Tree 
     */
    public static void testCleanEmptyFolders(){
        analyzer.buildFileTree("src/tests", false, true, 0);
        System.out.println(analyzer);
        analyzer.cleanEmptyFolders();
        System.out.println(analyzer);
    }
    
    public static void main(String[] args) {
        testBuildFileTree();
        testDuplicates();
        testCache();
        testDeletion();
        testGetFileNodeAndWeight();
        testCleanEmptyFolders();
        System.exit(0);    
    }
}
