import java.io.File;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Analyser services 
 * 
 * @author Valentin Bourcier
 */
public interface Analyzer {
    
    void buildFileTree(String rootPath, Filter filter, Boolean hash, Boolean recordInCache);
    
    void buildFileTree(String rootPath, Boolean hash, Boolean recordInCache);
    
    void deleteNode(String path);
    
    void deleteNode(DefaultMutableTreeNode node);
    
    Map<String, List<File>> getDuplicates(String path, Filter filter);
    
    Map<String, List<File>> getDuplicates(DefaultMutableTreeNode node, Filter filter);
    
    TreeModel getTreeModel();
    
    default void cleanCache(){
        CacheManager.getInstance().clean();
    }
    
    default void serializeCache(){
        CacheManager.getInstance().serialize();
    }
    
    default void unserializeCache(){
        CacheManager.getInstance().unserialize();
    }
    
    void listenSystemChanges(int millisRefresh);
    
    default void addFileTreeListener(FileTreeListener listener){
        SystemListener.SYSTEM_LISTENER.addFileTreeListener(listener);
    }
}
