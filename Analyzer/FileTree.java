package Analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Class which represent the file system Tree.
 * 
 * @author valentin
 */
public class FileTree implements Analyzer{
    
    private DefaultMutableTreeNode root;
    private CacheManager cache;
    
    /**
     * Tree builder
     */
    public FileTree() {
        this.cache = CacheManager.getInstance();
        this.root = new DefaultMutableTreeNode();
    }
    
    /**
     * Method allowing to get the tree root
     * @return A DefaultMutableTreeNode corresponding to the tree root
     */
    public DefaultMutableTreeNode getRoot(){
        return this.root;
    }
    
    /**
     * Method returning the FileNode contained on the indicated DefaultMutableTreeNode
     * @param path A string representation of the node path in the tree
     * @return FileNode contained by the node in argument
     */
    public FileNode getFileNode(String path){
        return getFileNode(getChildByPath(path));
    }
    
    /**
     * Method returning the FileNode contained on the indicated DefaultMutableTreeNode
     * @param node A DefaultMutableTreeNode containing a FileNode
     * @return FileNode contained by the node in argument
     */
    public synchronized FileNode getFileNode(DefaultMutableTreeNode node){
        return (FileNode) node.getUserObject();
    }
    
    /**
     * Method returning the tree depth
     * @return An Integer representing the max depth on the tree
     */
    public int getDepth(){
        return this.root.getDepth();
    }
    
    /**
     * Method getting a node from his path on the tree
     * Exemple: if root path is: "/home/", path could be "/session/.../". Path "/session/" is not valid, use getRoot()
     * @param path The string representation of the node path searched
     * @return The node in tree corresponding to the path.
     */
    public DefaultMutableTreeNode getChildByPath(String path){
        DefaultMutableTreeNode node = this.root;
        Enumeration<DefaultMutableTreeNode> en = node.preorderEnumeration();
        String[] nodePath = path.split(File.separator);
        String rootName = ((FileNode) this.root.getUserObject()).getName();
        String pathRoot = nodePath[0].equals("") ? nodePath[1] : nodePath[0];
        if(!rootName.equals(pathRoot)){
            throw new IllegalArgumentException("Child path should begin on tree root.");
        }
        int cpt = nodePath[0].equals("") ? 1 : 0;
        while (en.hasMoreElements() && cpt < nodePath.length) {
            DefaultMutableTreeNode tmp = en.nextElement();
            TreeNode[] tmpParent = null;
            String parentPath = "";
            if(!tmp.isRoot()){
                tmpParent = ((DefaultMutableTreeNode) tmp.getParent()).getPath();
                parentPath = tmpParent[tmpParent.length-1].toString();
            }
            TreeNode[] tmpPath = tmp.getPath();
            String name = tmpPath[tmpPath.length - 1].toString();
                
            if(!tmp.isLeaf()){
                if (name.equals(nodePath[cpt])) {
                    node = tmp;
                    cpt++;
                }
            }else if(cpt == nodePath.length-1 && name.equals(nodePath[cpt])){
                if(!tmp.isRoot() && parentPath.equals(nodePath[cpt-1])){
                    node = tmp;
                    cpt++;
                }
                
            }
        }
        if(rootName.equals(pathRoot) && node.equals(this.root) || cpt < nodePath.length-1){
            throw new IllegalArgumentException("Path cannot be verified by the tree.");
        }
        return node;
    }
    
    
    /**
     * Method allowing to get the weight of a tree/sub-tree, from a node path
     * @return The number of Bytes reprenseting the weight of the node.
     */
    public long getWeight(String path){
        return getWeight(getChildByPath(path));
    }
    
    /**
     * Method getting the weight of a tree/sub-tree
     * @return The number of Bytes reprenseting the weight of the node.
     */
    public long getWeight(DefaultMutableTreeNode node){
        long size = 0;
        Enumeration<DefaultMutableTreeNode> en = node.preorderEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode next = en.nextElement();
            FileNode file = ((FileNode) next.getUserObject());
            if(file.isFile()){
                size += file.length();
            }
        }
        return size;
    }
    
    
    /**
     * Method deleting empty folders in file tree
     */
    public void cleanEmptyFolders() {
        Enumeration<DefaultMutableTreeNode> en = this.root.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            if (node.getUserObject() instanceof File) {
                File file = (File) node.getUserObject();
                if(file.isDirectory() && file.listFiles().length == 0){               
                    node.removeFromParent();
                    en = this.root.breadthFirstEnumeration();
                }
                
            }

        }
    }
    
    /**
     * String representation of the file tree
     */
    @Override
    public String toString() {
        Enumeration<DefaultMutableTreeNode> en = this.root.preorderEnumeration();
        String tree = "";
        while (en.hasMoreElements())
        {
           DefaultMutableTreeNode node = en.nextElement();
           TreeNode[] path = node.getPath();
           tree = tree + (node.isLeaf() ? "  - " : "+ ") + path[path.length - 1] + "\n";
        }
        return tree; 
    }

    /**
     * Method returning duplicates files in a path
     * @param filter Filter used to check files
     * @param path String representation of the path on which you want to ckeck duplicates
     * @return Return list of duplicates indexed by hash in a map.
     */
    @Override
    public Map<String, List<File>> getDuplicates(String path, Filter filter) {
        this.unserializeCache();
        DuplicatesFinder finder = new DuplicatesFinder(Paths.get(path), filter);
        Future<Map<String, List<File>>> duplicated = Executors.newFixedThreadPool(1).submit(finder);
        try{
            this.serializeCache();
            return duplicated.get();
        }catch(Exception error){
            error.printStackTrace();
            System.out.println("Error while getting duplicates");
        }
        return null;
    }

    /**
     * Method allowing to check duplicates from a DefaultMutableTreeNode
     * @param filter Filter used to check files
     * @param path String representation of the path on which you want to ckeck duplicates
     * @return Return list of duplicates indexed by hash in a map.
     */
    @Override
    public Map<String, List<File>> getDuplicates(DefaultMutableTreeNode node, Filter filter) {
        return getDuplicates(getFileNode(node).getAbsolutePath(), filter);
    }

    /**
     * Method returning a TreeModel representation of the current FileTree
     * @return A TreeModel
     */
    @Override
    public TreeModel getTreeModel() {
        return new DefaultTreeModel(root);
    }

    /**
     * Builder of the File Tree
     * @param rootPath String representation of the root path
     * @param filter Filter used to check files
     * @param hash Boolean, equals true for hashing files on tree building, false either
     * @param recordInCache Boolean, equals true for saving tree files on cache.
     */
    public void buildFileTree(String rootPath, Filter filter, Boolean hash, Boolean recordInCache, int maxDepth){
        Path path = Paths.get(rootPath);
        this.unserializeCache();
        this.root = new DefaultMutableTreeNode(new FileNode(rootPath));
        FileTreeFactory factory = new FileTreeFactory(path, this.root, filter, hash, recordInCache, maxDepth);
        Future<DefaultMutableTreeNode> result = Executors.newSingleThreadExecutor().submit(factory);
        try{
            this.root = result.get();
            
        }catch(Exception error){
            error.printStackTrace();
            System.out.println("FileTree building failed");
        }
        if(filter.isActive()){
            this.cleanEmptyFolders();
        }
        if(recordInCache){
            this.serializeCache();
        }
    }
    
    /**
     * Default tree builder
     * @param rootPath String representation of the root path
     * @param hash Boolean, equals true for hashing files on tree building, false either
     * @param recordInCache Boolean, equals true for saving tree files on cache.
     */
    public void buildFileTree(String rootPath, Boolean hash, Boolean recordInCache, int maxDepth){
        buildFileTree(rootPath, new Filter(), hash, recordInCache, maxDepth);
    }
    
    /**
     * Method used to launch the semi real-time listening of the system (checking all N seconds)
     * @param millisRefresh Refreshing delay in milli-seconds
     */
    public void listenSystemChanges(int millisRefresh){
        SystemListener.SYSTEM_LISTENER.registerTree(this);
        SystemListener.SYSTEM_LISTENER.setDelay(millisRefresh);
        Thread listener = new Thread(SystemListener.SYSTEM_LISTENER);
        listener.start();
    }
    

    /**
     * Method removing a Node in file tree
     * @param path Path of the node
     */
    @Override
    public void deleteNode(String path) {
        deleteNode(getChildByPath(path));
    }

    /**
     * Method removing a Node in file tree
     * @param node DefaultMutableTreeNode instance to delete
     */
    
    @Override
    public void deleteNode(DefaultMutableTreeNode node) {
        Enumeration<DefaultMutableTreeNode> en = node.preorderEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode nextNode = en.nextElement();
            if(nextNode.isLeaf()){
                cache.remove(getFileNode(node).getAbsolutePath());
            }
        }
        node.removeAllChildren();
        node.removeFromParent();
        File file = getFileNode(node);
        if(file.exists()){
            try{
                delete(file);
            }catch(IOException error){
                error.printStackTrace();
            }
            
        }
    }
    
    /**
     * Method used to delete a file from the system
     * @param file The File to delete
     */
    private static void delete(File file) throws IOException {
        if(file.isDirectory()){
            for (File childFile : file.listFiles()) {
                if (childFile.isDirectory()) {
                    delete(childFile);
                } else {
                    if (!childFile.delete()) {
                        throw new IOException();
                    }
                }
            }
        }
        
        if (!file.delete()) {
            throw new IOException();
        }
    }

}
