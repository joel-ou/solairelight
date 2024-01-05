package io.github.joelou.solairelight.util;

import jdk.internal.misc.Unsafe;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

/**
 * @author Joel Ou
 */
public class ReactiveLinkedList<T> {

    private final LinkedList<T> list = new LinkedList<>();

    private final int capacity;

    private static final Unsafe U = Unsafe.getUnsafe();
    private static final long STATE
            = U.objectFieldOffset(ReactiveLinkedList.class, "state");

    private volatile int state;

    private volatile Thread holder;

    public ReactiveLinkedList(int capacity) {
        this.capacity = capacity;
        this.state = 0;
    }

    public void add(T t) {
        trySqueezeOut();
        for (; ;) {
            boolean acquired = compareAndSetState(0, 1);
            if(!acquired) {
                if (holder != null && !holder.isAlive()) {
                    //restore state
                    compareAndSetState(1, 0);
                }
                continue;
            }
            holder = Thread.currentThread();
            //recheck list size.
            trySqueezeOut();
            //add element
            list.addFirst(t);
            //release
            state = 0;
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

    private boolean compareAndSetState(int expect, int update){
        return U.compareAndSetInt(this, STATE, 0, 1);
    }
}
