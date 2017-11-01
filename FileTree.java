package Version2;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.tree.TreeNode;

/**
 * Classe qui permet de représenter une arborescence de fichier
 * 
 * @author valentin
 */
public class FileTree{
    
    private DefaultMutableTreeNode root;
    private FileNode fileRoot;
    private HashMap<String, Object> duplicates;
    
    /**
     * Constructeur de l'arbre
     * @param path Chemin du la racine
     * @param filter Filtre associé à la création de l'arbre
     */
    public FileTree(String path) {
        this.root = new DefaultMutableTreeNode(new FileNode(path));
        this.fileRoot = new FileNode(path);
        this.duplicates = new HashMap<>();
    }
    
    
    /**
     * Méthode qui permet de récupérer l'arbre par sa racine
     * @return La racine de l'arbre
     */
    public DefaultMutableTreeNode getRoot(){
        return this.root;
    }
    
    /**
     * Méthode qui permet de récupérer le fichier à la racine de l'arbre
     * @return L'objet FileNode qui correspond à la racine de l'arbre
     */
    public FileNode getFileRoot(){
        return this.fileRoot;
    }
    
    /**
     * Méthode qui permet de connaitre la profondeur de l'arbre
     * @return L'entier correspondant à la profondeur de l'arbre
     */
    public int getDepth(){
        return this.root.getDepth();
    }
    

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
    
    public void searchDuplicates(){
        Enumeration<DefaultMutableTreeNode> en = this.root.preorderEnumeration();
        while (en.hasMoreElements())
        {
            DefaultMutableTreeNode node = en.nextElement();
            FileNode element = ((FileNode) node.getUserObject());
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
        
    }
    
    public ArrayList<FileNode> getDuplicates(){
        ArrayList<FileNode> duplicated = new ArrayList<>();
        for (Object item : this.duplicates.values()) {
            if(item instanceof ArrayList){
                duplicated.addAll((ArrayList<FileNode>) item);
            }
        }
        return duplicated;
    }
    
    
    public void cleanFolders() {
        Enumeration<DefaultMutableTreeNode> en = this.root.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            if (node.getUserObject() instanceof File && ((File) node.getUserObject()).length() == 0 && node.isLeaf()) {
                node.removeAllChildren();
                node.removeFromParent();
                en = this.root.breadthFirstEnumeration();
            }

        }
    }
}
