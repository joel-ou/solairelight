package io.github.joelou.solairelight.event;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

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

    public static Future<?> invoke(Callable<?> callable){
        return forkJoinPool.submit(callable);
    }

    public static void invokeAll(List<Callable<Object>> callables){
        forkJoinPool.invokeAll(callables);
    }
}
