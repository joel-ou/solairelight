package io.github.joelou.solairelight.session;

import io.github.joelou.solairelight.properties.SolairelightProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
@Slf4j
public class CaffeineSessionStorage implements SessionStorage {

    private final Cache<String, BasicSession> sessionCaffeine;

    public CaffeineSessionStorage(SolairelightProperties solairelightProperties){
        int idleTime = solairelightProperties.getSession().getIdle();
        sessionCaffeine = Caffeine.newBuilder()
                .maximumSize(solairelightProperties.getSession().getMaxNumber())
                .expireAfterAccess(Duration.ofSeconds(idleTime))
                .scheduler(Scheduler.systemScheduler())
                .removalListener(new SessionRemovalCallback())
                .weakValues()
                .build();
    }

    @Override
    public void put(String key, BasicSession session) {
        this.sessionCaffeine.put(key, session);
        log.info("put session key: {} sessionId {}", key, session.getSessionId());
    }

    @Override
    public BasicSession get(String key) {
        return this.sessionCaffeine.getIfPresent(key);
    }

    @Override
    public Collection<BasicSession> getAll() {
        return sessionCaffeine.asMap().values();
    }

    @Override
    public Set<BasicSession> getAll(Set<String> keys) {
        if(keys == null) {
            return Collections.emptySet();
        }
        Set<BasicSession> sessions = keys.stream().map(this.sessionCaffeine::getIfPresent).collect(Collectors.toSet());
        log.info("found session {} by keys {}", sessions.size(), keys);
        return sessions;
    }

    @Override
    public void invalidate(String key) {
        this.sessionCaffeine.invalidate(key);
    }
}
