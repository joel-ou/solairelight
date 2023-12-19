package cn.muskmelon.event;

import java.util.concurrent.*;

/**
 * @author Joel Ou
 */
public class EventThreadPool {
    private final static ExecutorService threadPool;

    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();

    static {
        threadPool = new ThreadPoolExecutor(CPU_CORES*2,
                100,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000));
    }

    public static void execute(Runnable runnable){
        threadPool.execute(runnable);
    }
}
