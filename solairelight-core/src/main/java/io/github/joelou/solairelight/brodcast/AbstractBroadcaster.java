package io.github.joelou.solairelight.brodcast;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * @author Joel Ou
 */
@Slf4j
public abstract class AbstractBroadcaster implements Broadcaster {

    private final Cache<String, String> duplication;

    {
        duplication = Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .scheduler(Scheduler.systemScheduler())
                .weakValues()
                .build();
    }

    public void cache(String id){
        duplication.put(id, id);
    }


    public boolean duplicated(String id){
        return duplication.getIfPresent(id) != null;
    }
}
