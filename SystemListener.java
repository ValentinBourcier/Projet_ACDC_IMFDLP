import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author valentin
 */
public class SystemListener implements Runnable{
    
    public static SystemListener SYSTEM_LISTENER = new SystemListener();
    
    private FileTree tree;
    private CacheManager cache = CacheManager.getInstance();
    private int delay = 10000;
    private List<FileTreeListener> fileTreeListeners = new ArrayList<FileTreeListener>();
    
    
    public void registerTree(FileTree tree){
        this.tree = tree;
    }
    
    public void setDelay(int delay){
        this.delay = delay;
    }
    
    public void addFileTreeListener(FileTreeListener listener){
        this.fileTreeListeners.add(listener);
    }
    
    public synchronized void throwChangedEvent(){
        for (FileTreeListener fileTreeListener : fileTreeListeners) {
            fileTreeListener.fileTreeChanged();
        }
    }
    
    @Override
    public void run() {
        System.out.println("System listener started");
        while (true) {
            try{
                Thread.sleep(this.delay);
            }catch(InterruptedException error){
                System.out.println("System listening interrupted.");
            }
            boolean changed = false;
            Enumeration<DefaultMutableTreeNode> en = this.tree.getRoot().preorderEnumeration();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = en.nextElement();
                FileNode file = this.tree.getFileNode(node);
                String path = file.getAbsolutePath();
                if(cache.contains(path)){
                    node.setUserObject(this.cache.getMoreRecent(path));
                    changed = true; 
                }
            }
            
            if(changed){
                throwChangedEvent();
            }
        }
    }   
}
