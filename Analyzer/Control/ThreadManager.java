package Analyzer.Control;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadManager
{
    private static ThreadPoolExecutor executor;

    public static synchronized ThreadPoolExecutor getThread()
    {
        if (executor == null)
        {
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }
        return executor;
    }
}