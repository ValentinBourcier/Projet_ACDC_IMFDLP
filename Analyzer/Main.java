package Analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

import Analyzer.Control.CacheManager;
import Analyzer.Model.FileNode;
import Analyzer.Model.FileTree;
import Analyzer.Service.Analyzer;
import Analyzer.Service.Filter;
import sun.misc.IOUtils;

/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    
    public static Analyzer analyzer = new FileTree();
    
    
    public static void delete(File file) throws IOException {
    	for (File childFile : file.listFiles()) {
            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                if (!childFile.delete()) {
                    throw new IOException();
                }
            }
        }
    	file.delete();
    }
    
    private static void buildUnitTestTree() {
    	try {
			File dir = new File ("tests");
			if(dir.exists() && dir.isDirectory()) {
				delete(dir);
			}
			dir.mkdir();
			dir = new File("tests/folder1");
			dir.mkdir();
			dir = new File("tests/folder2");
			dir.mkdir();
			FileWriter file = new FileWriter(new File("tests/folder1/duplicated-file1"), true);
		    file.write("duplicates1");
		    file.close();
		    file = new FileWriter(new File("tests/folder1/duplicated-file2"), true);
		    file.write("duplicates2");
		    file.close();

			file = new FileWriter(new File("tests/folder2/duplicated-file1"), true);
		    file.write("duplicates1");
		    file.close();
		    file = new FileWriter(new File("tests/folder2/duplicated-file2"), true);
		    file.write("duplicates2");
		    file.close();
		    dir = new File("tests/folder2/empty-folder");
			dir.mkdir();
			
    	} catch(IOException io) {
    		io.printStackTrace();
    	}
    }
    
    /**
     * Build the file tree without hashing or storing files. 
     */
    public static boolean testBuildFileTree(){
        System.out.println("Building tree: ");
        analyzer.buildFileTree("tests", false, true, 0);
        System.out.println(analyzer);
        return true;
    }
    
    /**
     * Collecting duplicates without filtering it
     */
    public static boolean testDuplicates(){
        System.out.println("Searching duplicates: ");
        Map<String, List<File>> dup = analyzer.getDuplicates("tests", new Filter());
        for (String hash : dup.keySet()) {
            for (File file: dup.get(hash)) {
                System.out.println(hash + " -> " + file.getAbsolutePath());
            }
        }
        return true;
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static boolean testCache(){
        
        System.out.println("Deleting cache if exists");
        analyzer.cleanCache();
        
        // Building a FileTree in order to put some files in cache
        System.out.println("Filling cache");
        analyzer.buildFileTree("tests", false, true, 0);
        
        System.out.println("Cache is initialized: " + Files.exists(Paths.get(CacheManager.LOCATION)));
        return true;
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static boolean testDeletion(){
        System.out.println("Testing deletion: ");
        analyzer.buildFileTree("tests", false, true, 0);
        analyzer.deleteNode("tests/folder1/duplicated-file1");
        System.out.println("File deleted: " + !Files.exists(Paths.get("folder1/duplicated-file1")));
        return true;
    }
    
    /**
     * Cleaning cache, then test serialization
     */
    public static boolean testGetFileNodeAndWeight(){
        analyzer.buildFileTree("tests", false, true, 0);
        DefaultMutableTreeNode root = analyzer.getRoot();
        FileNode fileRoot = analyzer.getFileNode(root);
        System.out.println("FileNode == fileRoot: " + (fileRoot == ((FileNode) root.getUserObject())));
        System.out.println("Tree weight (33): " + analyzer.getWeight(root));
        return true;
    }
    
    /**
     * Deleting empty directories from File Tree 
     */
    public static boolean testCleanEmptyFolders(){
        analyzer.buildFileTree("tests", false, true, 0);
        System.out.println(analyzer);
        analyzer.cleanEmptyFolders();
        System.out.println(analyzer);
        return true;
    }
    
    public static void main(String[] args) {

    	if(args.length > 0 && args[0].equals("-t")) {
    		buildUnitTestTree();
            testBuildFileTree();
            testDuplicates();
            testCache();
            testDeletion();
            testGetFileNodeAndWeight();
            testCleanEmptyFolders();
            try {
    			delete(new File("tests"));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
        System.exit(0);    
    }
}
