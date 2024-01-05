package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.util.CapacityLimitLinkedList;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joel Ou
 */
@Slf4j
public abstract class AbstractBroadcaster implements Broadcaster {

    private final CapacityLimitLinkedList<String> duplication;

    {
        duplication = new CapacityLimitLinkedList<>(1000);
    }

    public void cache(String id){
        duplication.add(id);
    }


    public boolean duplicated(String id){
        return duplication.contains(id);
    }
}
