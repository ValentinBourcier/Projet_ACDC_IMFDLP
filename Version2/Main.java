package Version2;

/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    public static void main(String[] args) {
        FileTree tree = FileTreeFactory.createFileTree("/home/valentin/files/");
        tree.searchDuplicates();
        for (FileNode file: tree.getDuplicates()) {
            System.out.println("Duplicata: " + file.getValue().getAbsolutePath());
        }
        System.exit(0);
        
    }
}
