package Analyzer.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import Analyzer.Control.CacheManager;
import Analyzer.Control.FileTreeListener;
import Analyzer.Control.SystemListener;
import Analyzer.Model.FileNode;

/**
 * Analyser services 
 * 
 * @author Valentin Bourcier
 */
public interface Analyzer {
    
    
	/**
     * Builder of the File Tree
     * @param rootPath String representation of the root path
     * @param filter Filter used to check files
     * @param hash Boolean, equals true for hashing files on tree building, false either
     * @param recordInCache Boolean, equals true for saving tree files on cache.
     * @return The DefaultMutableTreeNode root of the Tree in a thread result object.
     */
	Future<DefaultMutableTreeNode> buildFileTree(String rootPath, Filter filter, Boolean hash, Boolean recordInCache, int maxDepth);
    
    /**
     * Default tree builder
     * @param rootPath String representation of the root path
     * @param hash Boolean, equals true for hashing files on tree building, false either
     * @param recordInCache Boolean, equals true for saving tree files on cache.
     * @return The DefaultMutableTreeNode root of the Tree in a thread result object.
     */
	Future<DefaultMutableTreeNode> buildFileTree(String rootPath, Boolean hash, Boolean recordInCache, int maxDepth);
    
    /**
     * Method returning duplicates files in a path
     * @param filter Filter used to check files
     * @param path String representation of the path on which you want to ckeck duplicates
     * @return Return list of duplicates indexed by hash in a map.
     */
    Map<String, List<File>> getDuplicates(String path, Filter filter);
    
    /**
     * Method allowing to check duplicates from a DefaultMutableTreeNode
     * @param filter Filter used to check files
     * @param node Node on which you want to ckeck duplicates
     * @return Return list of duplicates indexed by hash in a map.
     */
    Map<String, List<File>> getDuplicates(DefaultMutableTreeNode node, Filter filter);
    
    /**
     * Method returning a TreeModel representation of the current FileTree
     * @return A TreeModel
     */
    TreeModel getTreeModel();
    
    /**
     * Method deleting cache from system and reseting cache in JVM
     */
    default void cleanCache(){
        CacheManager.getInstance().clean();
    }
    
    /**
     * Method which serialize the cache 
     */
    default void serializeCache(){
        CacheManager.getInstance().serialize();
    }
    
    /**
     * Method unserializing the cache
     */
    default void unserializeCache(){
        CacheManager.getInstance().unserialize();
    }
    
    /**
     * Method used to launch the semi real-time listening of the system (checking all N seconds)
     * @param millisRefresh Refreshing delay in milli-seconds
     */
    void listenSystemChanges(int millisRefresh);
    

    /**
     * Method to add a listener on FileTree updates
     * @param listener Instance of FileTreeListener
     */
    default void addFileTreeListener(FileTreeListener listener){
        SystemListener.SYSTEM_LISTENER.addFileTreeListener(listener);
    }
    
    
    /**
     * Method allowing to get the weight of a tree/sub-tree, from a node path
     * @return The number of Bytes reprenseting the weight of the node.
     */
    long getWeight(String path);
    
    /**
     * Method getting the weight of a tree/sub-tree
     * @return The number of Bytes reprenseting the weight of the node.
     */
    long getWeight(DefaultMutableTreeNode node);
    
    /**
     * Method returning the FileNode contained on the indicated DefaultMutableTreeNode
     * @param path A string representation of the node path in the tree
     * @return FileNode contained by the node in argument
     */
    FileNode getFileNode(String path);
    
    /** Method returning the FileNode contained on the indicated DefaultMutableTreeNode
     * @param node A DefaultMutableTreeNode containing a FileNode
     * @return FileNode contained by the node in argument
     */
    FileNode getFileNode(DefaultMutableTreeNode node);
    
    /**
     * Method removing a Node in file tree
     * @param path Path of the node
     */
    void deleteNode(String path);
    
    /**
     * Method removing a Node in file tree
     * @param node DefaultMutableTreeNode instance to delete
     */
    void deleteNode(DefaultMutableTreeNode node);
    
    /**
     * Method returning the tree depth
     * @return An Integer representing the max depth on the tree
     */
    int getDepth();
    
    /**
     * Method deleting empty folders in file tree
     */
    void cleanEmptyFolders();
    
    /**
     * Method getting a node from his path on the tree
     * Exemple: if root path is: "/home/", path could be "/session/.../". Path "/session/" is not valid, use getRoot()
     * @param path The string representation of the node path searched
     * @return The node in tree corresponding to the path.
     */
    DefaultMutableTreeNode getChildByPath(String path);
    
    /**
     * Method allowing to get the tree root
     * @return A DefaultMutableTreeNode corresponding to the tree root
     */
    DefaultMutableTreeNode getRoot();
    
}
