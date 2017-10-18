import java.io.File;
import java.io.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.concurrent.Callable;

/**
 * Classe qui permet de représenter une arborescence de fichier
 * 
 * @author valentin
 */
public class FileTree implements Callable{

    DefaultMutableTreeNode root;
    FileNode fileRoot;
    FileFilter filter;
    
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
    
    /**
     * Surcharge du constructeur ave filtre par défaut
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
                    DefaultMutableTreeNode directory = new DefaultMutableTreeNode(new FileNode(file));
                    this.addChild(directory, file.getPath());
                    root.add(directory);
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
     * Initialisation de la construction de l'arbre en parallèle de l'exécution principale
     * on évitera ainsi de bloquer l'exécution du programme et l'affichage de l'arborescence
     * dans la future interface.
     */
    @Override
    public Object call() throws Exception {
        addChild(this.root, this.fileRoot.getAbsolutePath());
        return null;
    }
    

}
