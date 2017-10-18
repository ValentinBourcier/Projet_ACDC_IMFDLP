import java.io.File;
import java.util.ArrayList;
import java.util.logging.Filter;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author valentin
 */
public interface INode {
    
    public INode tree(String path); 
    public INode tree(String path, int depth); 
    public ArrayList<File> doublons();
    public DefaultTreeModel treeModel(); 
    public String filename();
    public String hash(); 
    public long weight();
    public String absolutePath(); 
    public ArrayList<INode> child();
    public INode filter(Filter[] filters);
    
}
