package io.github.joelou.solairelight.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
public class CASMutexes {
    private final AtomicInteger state = new AtomicInteger(0);

    public void execute(Runnable runnable){
        for (; ;) {
            boolean acquired = state.compareAndSet(0, 1);
            if(!acquired) {
                continue;
            }
            try {
                runnable.run();
            } finally {
                //restore state.
                state.set(0);
            }
            return;
        }
    }

    public void timed(Runnable runnable, Duration duration){
        long start = System.nanoTime();
        while ((System.nanoTime()-start) < duration.toNanos()) {
            boolean acquired = state.compareAndSet(0, 1);
            if(!acquired) {
                continue;
            }
            try {
                runnable.run();
            } finally {
                //restore state.
                state.set(0);
            }
            return;
        }
    }
}
