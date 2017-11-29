package Analyzer.Control;

import Analyzer.Model.FileTree;

/**
 * Interface to implement for listening changes in the FileTree
 * 
 * @author Valentin Bourcier
 */
public interface FileTreeListener {
    void fileTreeUpdated(FileTree tree);
}
