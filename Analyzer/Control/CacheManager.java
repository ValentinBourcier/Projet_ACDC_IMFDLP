package Analyzer.Control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import Analyzer.Model.FileNode;

/**
 * Class managing cache file
 * 
 * @author Valentin Bourcier
 */
public class CacheManager
{

    private HashMap<String, FileNode> cache = new HashMap<String, FileNode>();
    public static String LOCATION = "tree.cache";
    private Boolean modified;
    private static CacheManager manager;

    /**
     * Cache initialization
     */
    private CacheManager()
    {
        cache = new HashMap<String, FileNode>();
        modified = false;
    }

    /**
     * Thread safe singleton implementation
     * @return The cache manager instance
     */
    public static synchronized CacheManager getInstance()
    {
        if (manager == null)
        {
            manager = new CacheManager();
        }
        return manager;
    }

    /**
     * Method to add a file in cache
     * @param file FileNode instance to save
     */
    public void add(FileNode file)
    {
        cache.put(file.getAbsolutePath(), file);
        modified = true;
    }

    /**
     * Method removing a file from cache
     * @param path Path of the file to remove
     */
    public void remove(String path)
    {
        cache.remove(path);
        modified = true;
    }

    /**
     * Method collecting 
     * @param path Path of the file to remove
     * @return FileNode instance of file at path
     */
    public FileNode get(String path)
    {
        if (isSet())
        {
            return cache.get(path);
        }
        throw new UnsupportedOperationException("Cache is not Set.");
    }

    /**
     * Method checking if cache is set
     * @return True if the cache is saved in system
     */
    public boolean isSet()
    {
        return Files.exists(Paths.get(LOCATION));
    }

    public boolean contains(String path)
    {
        if (isSet())
        {
            return cache.containsKey(path);
        }
        return false;
    }

    public FileNode getMoreRecent(String path)
    {
        FileNode cacheNode = get(path);
        FileNode systemNode = new FileNode(path);
        return cacheNode.INSTANCE_TIME > systemNode.lastModified() ? cacheNode : systemNode;
    }

    /**
     * Method updating a file in cache
     * @param path Path of the file to update
     */
    public void update(String path)
    {
        cache.put(path, getMoreRecent(path));
    }

    /**
     * Method which serialize the cache 
     */
    public void serialize()
    {
        if (modified)
        {
            try
            {
                FileOutputStream file = new FileOutputStream(LOCATION, false);
                ObjectOutputStream stream = new ObjectOutputStream(file);
                stream.writeObject(cache);
                stream.close();
                file.close();
            }
            catch (IOException error)
            {
                error.printStackTrace();
            }
        }
    }

    /**
     * Method unserializing the cache
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void unserialize()
    {
        if (isSet())
        {
            try
            {
                FileInputStream file = new FileInputStream(LOCATION);
                ObjectInputStream stream = new ObjectInputStream(file);
                cache = (HashMap) stream.readObject();
                stream.close();
                file.close();
            }
            catch (IOException error)
            {
                error.printStackTrace();
            }
            catch (ClassNotFoundException error)
            {
                error.printStackTrace();
            }
        }
    }

    /**
     * Method deleting cache from system and reseting cache in JVM
     */
    public void clean()
    {
        if (isSet())
        {
            new File(LOCATION).delete();
        }
    }

}
