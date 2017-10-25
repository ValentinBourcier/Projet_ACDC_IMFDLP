
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valentin
 */
public class FileTreeConcreteFactory implements FileTreeFactory{
    
    private static ThreadPoolExecutor threadPool;
    private Future<FileTree> fileTree;

    public FileTreeConcreteFactory() {
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }
    
    @Override
    public FileTree createFileTree(String rootPath, Filter filter) {
        fileTree = threadPool.submit(new FileTree(rootPath, filter));
        try{
           return fileTree.get();
        }catch(InterruptedException error){
            System.out.println("Construction interrompue.");
        }catch(ExecutionException error){
            System.err.println("Erreur lors de la construction: " + error.getMessage());
        }
        return null;
        
    }

    @Override
    public FileTree createFileTree(String rootPath) {
        return createFileTree(rootPath, new Filter());
    }
    
    
}
