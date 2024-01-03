package io.github.joelou.solairelight.event;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Joel Ou
 */
public class EventThreadPool {
    private final static ForkJoinPool forkJoinPool;

    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();

    static {
        forkJoinPool = new ForkJoinPool
                (CPU_CORES*2,
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
    }

    public static void execute(Runnable runnable){
        forkJoinPool.execute(runnable);
    }

    public static Future<?> invokeAll(Callable<?> callable){
        return forkJoinPool.submit(callable);
    }

    public static void invokeAll(List<Callable<Object>> callables){
        forkJoinPool.invokeAll(callables);
    }
}
