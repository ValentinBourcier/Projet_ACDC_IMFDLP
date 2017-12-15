package Analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;

import Analyzer.Control.FileTreeListener;
import Analyzer.Model.FileTree;
import Analyzer.Service.Analyzer;
import Analyzer.Service.Filter;

public class Command {

	private boolean hash = false;
	private boolean recordInCache = false;
	private int maxDepth = 0;
	private boolean print = false;
	private int delay = 60000;
	private String command = null;
	private String path = null;
	private String[] args;
	
	public Command(String[] args) {
		this.args = args;
	}
	
	public boolean parse() {
		for (int i=0; i < args.length; i++) {
			if(args[i].equals("-cache")) {
				recordInCache = true;
			}
			
			if(args[i].equals("-hash")) {
				hash = true;
			}
			
			if(args[i].equals("test")) {
				Test.launchTests();
				System.exit(0);
			}
			
			if(args[i].equals("help")) {
				help();
				System.exit(0);
			}
			
			
			if(args[i].equals("-print")) {
				print = true;
			}
			
			if(args[i].equals("-mD")) {
				if(!args[i+1].contains("-")) {
					try {
						maxDepth = Integer.parseInt(args[i+1]);
						i++;
					}catch(Exception error) {
						System.out.println("Error while executing command, -mD, maxDepth should be an integer.");
						return false;
					}
				}else {
					return false;
				}
			}
			
			if(args[i].equals("-d")) {
				if(!args[i+1].contains("-")) {	
	    			try {
	    				delay = Integer.parseInt(args[i+1]);
	    				i++;
	    			}catch(Exception error) {
	    				System.out.println("Error while executing command, -d, delay should be an integer.");
	    				return false;
	    			}
	    		}else {
	    			return false;
				}
			}
			
			if(args[i].equals("-p")) {
				if(!args[i+1].contains("-")) {
					path = args[i+1];
					i++;
	    		}else {
	    			return false;
				}
			}
			
			if(args[i].equals("-c")) {
				if(!args[i+1].contains("-")) {
					command = args[i+1];
					i++;
	    		}else {
	    			return false;
				}
			}
		}
		
		if(path == null && command == null) {
			return false;
		}
		return true;
	}
	
	private class Listener implements FileTreeListener{

		@Override
		public void fileTreeUpdated(FileTree tree, int nbChanges) {
			System.out.println("FileTree changed... Now listening for new changes.");
			System.out.println(tree);
		}
		
	}
	
	public void execute() {
		if(parse()) {
			Analyzer analyzer = new FileTree();
			if(path != null) {
				analyzer.buildFileTree(path, hash, recordInCache, maxDepth);				
			}
			
			switch (command) {
			
			case "duplicates":
				System.out.println("Duplicates contained in \"" + path + "\" are: ");
				Map<String, List<File>> dup = analyzer.getDuplicates(path, new Filter());
				for (String vHash : dup.keySet()) {
					for (File file: dup.get(vHash)) {
						System.out.println(vHash + " -> " + file.getAbsolutePath());
					}
				}
				break;
				
			case "weight":
				System.out.println("Weight of the folder \"" + path + "\" is: " + analyzer.getWeight(analyzer.getRoot()));
				break;
				
			case "depth":
				System.out.println("Depth of the tree \"" + path + "\" is: " + analyzer.getDepth());
				break;
				
			case "listenSystem":
				System.out.println("Listening system...");
				analyzer.addFileTreeListener(new Listener());
				analyzer.listenSystemChanges(delay);
				break;
				
			case "cleanCache":
				analyzer.cleanCache();
				break;
				
			case "help":
				help();
				break;
				
			default:
				System.out.println("Sorry, command you've asked for is unknown.");
				break;
			}
			
			if(print) {
				System.out.println(analyzer);
			}
		}else {
			help();
		}
	}
	
	public void help() {
		System.out.println("# ILMFDLP help page: \n");
		
		System.out.println("##### Command list #####");
		
		System.out.println("# duplicates   -> Getting duplicates from a root path.");
		System.out.println("# weight       -> Return the weight of the directory path.");
		System.out.println("# depth        -> Getting the depth of tree builded from the path.");
		System.out.println("# listenSystem -> Listen system changes. Require -cache option.");
		System.out.println("# cleanCache   -> Command which is cleaning the tree cache.");
		
		System.out.println("####### Options #######");
		
		System.out.println("# -hash   -> Hash files while building the tree. (default: false)");
		System.out.println("# -cache  -> Record files in cache. (default: false)");
		System.out.println("# -print  -> Print the tree while ending the command (default: false)");
		System.out.println("# -mD ..  -> Integer corresponding to the max depth of tree you want to build (default: none, 0)");
		System.out.println("# -d ..   -> Millisecond delay between to checking of the FileTree changes. (default: 60000)");
		System.out.println("# -p ..   -> Required most of the time (not with test, help commands). Path of your folder, tree.");
		
	}
	
}
