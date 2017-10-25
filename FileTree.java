import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.tree.TreeNode;

/**
 * Classe qui permet de représenter une arborescence de fichier
 * 
 * @author valentin
 */
public class FileTree implements Callable{
    
    private List<Future<FileTree>> threadsResultsList = new ArrayList<>();
    
    private DefaultMutableTreeNode root;
    private FileNode fileRoot;
    private FileFilter filter;
    private MultiFileCollection duplicates;
    
    /**
     * Constructeur de l'arbre
     * @param path Chemin du la racine
     * @param filter Filtre associé à la création de l'arbre
     */
    public FileTree(String path, FileFilter filter) {
        this.root = new DefaultMutableTreeNode();
        this.fileRoot = new FileNode(path);
        this.filter = filter;
    }
    
    
    private static class ExecutorManager
    {		
        public static final int MAX_THREADS = 3;
        public static int NB_THREADS = 0;
        private static ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
    }

    public static ThreadPoolExecutor getExecutorInstance()
    {
            return ExecutorManager.EXECUTOR;
    }
    
    /**
     * Surcharge du constructeur avec filtre par défaut
     * @param path Chemin de la racine
     */
    public FileTree(String path) {
        this(path, new Filter());
    }
 
    /**
     * Méthode qui permet de récupérer la liste des sous-fichiers d'un dossier
     * @param path Chemin du dossier à explorer
     */
    public File[] getFiles(String path) {
        File file = new File(path);
        return file.listFiles(this.filter);
    }

    /**
     * Méthode qui permet de créer une arborescence de fichiers
     * @param root Racine de l'arbre courant
     * @param path Chemin de la racine de l'arbre
     */
    public void addChild(DefaultMutableTreeNode root, String path) {
        File[] files = this.getFiles(path);
        for(File file: files) {
            if(file.canRead()){
                if(file.isDirectory()) {
                    if(ExecutorManager.NB_THREADS < ExecutorManager.MAX_THREADS){
                        ExecutorManager.NB_THREADS++;
                        Future<FileTree> result = getExecutorInstance().submit(new FileTree(file.getAbsolutePath()));
                        threadsResultsList.add(result);
                        for (Future<FileTree> fileTree : threadsResultsList) {
                            try{
                                root.add(fileTree.get().root());
                            }catch (InterruptedException error){
                                System.out.println("Thread de construction de l'arborescence interrompu.");
                            }catch(ExecutionException error){
                                System.out.println("L'exécution du thread de construction de l'arborescence a rencontré une erreur: " + error.getMessage());
                            }
                        }
                        getExecutorInstance().shutdown();
                    }else{
                        FileNode fileNode = new FileNode(file);
                        this.duplicates.putFile(fileNode.getHash(), fileNode); // TODO: Vérifier les accès asynchrones
                        DefaultMutableTreeNode directory = new DefaultMutableTreeNode(fileNode);
                        this.addChild(directory, file.getPath());
                        root.add(directory);
                    }
                    
                } else {
                    root.add(new DefaultMutableTreeNode(new FileNode(file)));
                }
            }
        }
    }
    
    /**
     * Méthode qui permet de récupérer l'arbre par sa racine
     * @return La racine de l'arbre
     */
    public DefaultMutableTreeNode root(){
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
        return this.root().getDepth();
    }
    
    
    /**
     * Méthode qui permet d'obtenir la liste des fichiers dupliqués
     */
    public List<FileNode> getDuplicates(){
        return this.duplicates.getDuplicates();
    }
    
    /**
     * Initialisation de la construction de l'arbre en parallèle de l'exécution principale
     * on évitera ainsi de bloquer l'exécution du programme et l'affichage de l'arborescence
     * dans la future interface.
     */
    @Override
    public FileTree call() throws Exception {
        addChild(this.root, this.fileRoot.getAbsolutePath());
        return this;
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

}
