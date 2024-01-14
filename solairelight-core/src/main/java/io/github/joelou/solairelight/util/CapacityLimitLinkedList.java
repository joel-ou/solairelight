package io.github.joelou.solairelight.util;


import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sun.misc.Unsafe;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
public class CapacityLimitLinkedList<T> {

    @Getter
    private final LinkedList<T> list = new LinkedList<>();

    private final int capacity;

    private final AtomicInteger state = new AtomicInteger(0);

    private volatile Thread holder;

    private volatile long duration;

    public CapacityLimitLinkedList(int capacity) {
        this.capacity = capacity;
    }

    public void add(T t) {
        for (; ;) {
            boolean acquired = state.compareAndSet(0, 1);
            if(!acquired) {
                continue;
            }
            try {
                duration = System.currentTimeMillis();
                holder = Thread.currentThread();
                //check list size.
                trySqueezeOut();
                //add element
                list.addFirst(t);
            } finally {
                //restore state.
                state.set(0);
            }
            return;
        }
    }

    public Mono<Boolean> containsMono(T t){
        return Mono.create(sink->sink.success(list.contains(t)));
    }

    public boolean contains(T t){
        return list.contains(t);
    }

    private void trySqueezeOut(){
        if(capacity == list.size()) {
            list.removeLast();
        }
    }
}
