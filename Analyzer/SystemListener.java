package Analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class which allow to listen the system files changes
 * 
 * @author Valentin Bourcier
 */
public class SystemListener implements Runnable{
    
    public static SystemListener SYSTEM_LISTENER = new SystemListener();
    
    private FileTree tree;
    private CacheManager cache = CacheManager.getInstance();
    private int delay = 10000;
    private List<FileTreeListener> fileTreeListeners = new ArrayList<FileTreeListener>();
    
    
    /**
     * Method to set the tree that will be updated
     * @param tree FileTree to update on system changes
     */
    public void registerTree(FileTree tree){
        this.tree = tree;
    }
    
    /**
     * Method setting the refreshing delay
     * @param delay Refreshing delay in milliseconds
     */
    public void setDelay(int delay){
        this.delay = delay;
    }
    
    /**
     * Method to add a listener on FileTree updates
     * @param listener Instance of FileTreeListener
     */
    public void addFileTreeListener(FileTreeListener listener){
        this.fileTreeListeners.add(listener);
    }
    
    /**
     * Method throwing the update Event
     */
    public synchronized void throwUpdateEvent(){
        for (FileTreeListener fileTreeListener: fileTreeListeners) {
            fileTreeListener.fileTreeUpdated(this.tree);
        }
    }
    
    /**
     * Method listening on the system changes
     */
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
                File sysFile = new File(path);
                if(cache.contains(path) && cache.get(path).INSTANCE_TIME > sysFile.lastModified()){
                    node.setUserObject(this.cache.getMoreRecent(path));
                    changed = true;
                }
            }
            
            if(changed){
                throwUpdateEvent();
            }
        }
    }   
}
