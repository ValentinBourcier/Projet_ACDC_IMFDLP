/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valentin
 */
public interface FileTreeFactory {
    
    public FileTree createFileTree(String rootPath, Filter filter);
    public FileTree createFileTree(String rootPath);
    
}
