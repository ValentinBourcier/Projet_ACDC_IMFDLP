

/**
 * Lancement de tests
 * 
 * @author valentin
 */
public class Main {
    public static void main(String[] args) {
//        FileTree tree = FileTreeFactory.createFileTree("/home/valentin/files/");
//        tree.searchDuplicates();
//        for (FileNode file: tree.getDuplicates()) {
//            System.out.println("Duplicata: " + file.getValue().getAbsolutePath());
//        }
        DuplicatesFinder finder = DuplicatesFinder.searchDuplicates("/home/valentin/", new Filter());
        System.exit(0);
        
    }
}
