import java.io.File;
import java.io.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.concurrent.Callable;
/**
 *
 * @author valentin
 */
public class FileTree implements Callable{

    DefaultMutableTreeNode root;
    FileNode fileRoot;
    FileFilter filter;

    public FileTree(String path, FileFilter filter) {
        this.root = new DefaultMutableTreeNode();
        this.fileRoot = new FileNode(path);
        this.filter = filter;
    }
    
    public FileTree(String path) {
        this(path, new Filter());
    }
 
    public File[] getFiles(String path) {
        File file = new File(path);
        return file.listFiles(this.filter);
    }

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
    
    public DefaultMutableTreeNode root(){
        return this.root;
    }
    
    public FileNode getFileRoot(){
        return this.fileRoot;
    }
    
    public int getDepth(){
        return this.root().getDepth();
    }
    
    @Override
    public Object call() throws Exception {
        addChild(this.root, this.fileRoot.getAbsolutePath());
        return null;
    }
    

}
