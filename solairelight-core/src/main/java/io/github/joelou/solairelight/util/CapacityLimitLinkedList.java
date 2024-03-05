package io.github.joelou.solairelight.util;


import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 * this class is a thread spin-lock collection.
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

    public boolean add(T t) {
        if(contains(t))return false;
        for (; ;) {
            boolean acquired = state.compareAndSet(0, 1);
            if(!acquired) {
                continue;
            }
            try {
                if(contains(t))return false;
                duration = System.currentTimeMillis();
                holder = Thread.currentThread();
                //check list size.
                trySqueezeOut();
                //add element
                list.addFirst(t);
                return true;
            } finally {
                //restore state.
                state.set(0);
            }
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

    public void remove(T t){
        list.remove(t);
    }
}
