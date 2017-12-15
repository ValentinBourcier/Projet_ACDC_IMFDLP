package Analyzer.Control;

import java.util.ArrayList;

public class ErrorManager {
	
	private static ArrayList<ErrorHandler> handlers = new ArrayList<ErrorHandler>();
	
	public static void addHandler(ErrorHandler handler) {
		handlers.add(handler);
	}
	
	public static void throwError(Exception exception) {
		for (ErrorHandler errorHandler : handlers) {
			errorHandler.capturedError(exception);
		}
	}
	
}
