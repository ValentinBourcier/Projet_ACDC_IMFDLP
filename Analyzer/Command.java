package Analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import Analyzer.Control.ErrorManager;
import Analyzer.Control.FileTreeListener;
import Analyzer.Model.FileTree;
import Analyzer.Service.Analyzer;
import Analyzer.Service.Filter;

/**
 * Class checking user launching command
 * 
 * @author Valentin Bourcier
 */

public class Command
{

    private boolean hash = false;
    @SuppressWarnings("unused")
    private boolean recordInCache = false;
    @SuppressWarnings("unused")
    private int maxDepth = 0;
    private boolean print = false;
    private int delay = 60000;
    private String command = null;
    private String path = null;
    private String[] args;
    private boolean thread = false;
    private String regexp = null;

    public Command(String[] aArgs)
    {
        args = aArgs;
    }

    public boolean parse()
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-cache"))
            {
                recordInCache = true;
            }

            if (args[i].equals("-hash"))
            {
                hash = true;
            }

            if (args[i].equals("-thread"))
            {
                thread = true;
            }

            if (args[i].equals("-filter"))
            {
                if (!args[i + 1].startsWith("-"))
                {
                    regexp = args[i + 1];
                    regexp = regexp.replaceAll("\"", "");
                    i++;
                }
                else
                {
                    return false;
                }
            }

            if (args[i].equals("test"))
            {
                Test.launchTests();
                System.exit(0);
            }

            if (args[i].equals("help"))
            {
                help();
                System.exit(0);
            }

            if (args[i].equals("-print"))
            {
                print = true;
            }

            if (args[i].equals("-mD"))
            {
                if (!args[i + 1].startsWith("-"))
                {
                    try
                    {
                        maxDepth = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    catch (Exception error)
                    {
                        System.out.println("Error while executing command, -mD, maxDepth should be an integer.");
                        ErrorManager.throwError(error);
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }

            if (args[i].equals("-d"))
            {
                if (!args[i + 1].startsWith("-"))
                {
                    try
                    {
                        delay = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    catch (Exception error)
                    {
                        System.out.println("Error while executing command, -d, delay should be an integer.");
                        ErrorManager.throwError(error);
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }

            if (args[i].equals("-p"))
            {
                if (!args[i + 1].startsWith("-"))
                {
                    path = args[i + 1];
                    i++;
                }
                else
                {
                    return false;
                }
            }

            if (args[i].equals("-c"))
            {
                if (!args[i + 1].startsWith("-"))
                {
                    command = args[i + 1];
                    i++;
                }
                else
                {
                    return false;
                }
            }
        }

        if (path == null && command == null)
        {
            return false;
        }
        else
        {
            if (path != null)
            {
                path = path.replaceAll("\"", "");
            }
            if (command != null)
            {
                command = command.replaceAll("\"", "");
            }
        }
        return true;
    }

    private class Listener implements FileTreeListener
    {

        @Override
        public void fileTreeUpdated(FileTree tree, int nbChanges)
        {
            System.out.println(nbChanges + " items changed in FileTree... Now listening for new changes.");
            System.out.println(tree);
        }

    }

    public void execute()
    {
        if (parse())
        {
            Analyzer analyzer = new FileTree();
            if (path != null)
            {
                if (regexp != null)
                {
                    Filter filter = new Filter();
                    filter.setPattern(regexp);
                    analyzer.buildFileTree(path, filter, thread, hash, recordInCache, maxDepth);
                }
                else
                {
                    analyzer.buildFileTree(path, thread, hash, recordInCache, maxDepth);
                }
            }

            if (command != null)
            {

                if (command.equals("duplicates"))
                {

                    System.out.println("Duplicates contained in \"" + path + "\" are: ");
                    Map<String, List<File>> dup = analyzer.getDuplicates(path, new Filter());
                    for (String vHash : dup.keySet())
                    {
                        for (File file : dup.get(vHash))
                        {
                            System.out.println(vHash + " -> " + file.getAbsolutePath());
                        }
                    }

                }
                else if (command.equals("weight"))
                {

                    System.out.println("Weight of the folder \"" + path + "\" is: " + analyzer.getWeight(analyzer.getRoot()));

                }
                else if (command.equals("depth"))
                {

                    System.out.println("Depth of the tree \"" + path + "\" is: " + analyzer.getDepth());

                }
                else if (command.equals("listenSystem"))
                {

                    System.out.println("Listening system...");
                    analyzer.addFileTreeListener(new Listener());
                    analyzer.listenSystemChanges(delay);

                }
                else if (command.equals("cleanCache"))
                {

                    analyzer.cleanCache();

                }
                else if (command.equals("ihm"))
                {

                    JFrame frame = new JFrame("IHM");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    JTree jtree = new JTree(analyzer.getTreeModel());
                    jtree.setShowsRootHandles(true);
                    JScrollPane scrollPane = new JScrollPane(jtree);
                    frame.add(scrollPane);
                    frame.setLocationByPlatform(true);
                    frame.setSize(640, 480);
                    frame.setVisible(true);

                }
                else if (command.equals("help"))
                {
                	String message = "Sorry, command you've asked for is unknown.";
                    System.out.println(message);
                    ErrorManager.throwError(new Exception(message));
                    help();

                }

            }
            if (print && path != null)
            {
                System.out.println(analyzer);

            }
        }
        else
        {
            help();
        }
    }

    public void help()
    {
        System.out.println("# ILMFDLP help page: \n");

        System.out.println("##### Command list #####");

        System.out.println("# duplicates   -> Getting duplicates from a root path.");
        System.out.println("# weight       -> Return the weight of the directory path.");
        System.out.println("# depth        -> Getting the depth of tree builded from the path.");
        System.out.println("# listenSystem -> Listen system changes. Require -cache option.");
        System.out.println("# cleanCache   -> Command which is cleaning the tree cache.");
        System.out.println("# ihm          -> Command allowing to test the graphic rendering of the tree.");

        System.out.println("####### Options #######");

        System.out.println("# -c ..        -> Command you want to execute.");
        System.out.println("# -thread      -> Use thread to build the FileTree. (default: false)");
        System.out.println("# -hash        -> Hash files while building the tree. (default: false)");
        System.out.println("# -cache       -> Record files in cache. (default: false)");
        System.out.println("# -print       -> Print the tree while ending the command (default: false)");
        System.out.println("# -mD ..       -> Integer corresponding to the max depth of tree you want to build (default: none, 0)");
        System.out.println("# -d ..        -> Millisecond delay between to checking of the FileTree changes. (default: 60000)");
        System.out.println("# -p ..        -> Required most of the time (not with test, help commands). Path of your folder, tree.");
        System.out.println("# -filter ..   -> Regex used to restrict the tree construction. ");

    }

}
