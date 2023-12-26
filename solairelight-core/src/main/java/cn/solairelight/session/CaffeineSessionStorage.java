package cn.solairelight.session;

import cn.solairelight.properties.SolairelightProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
@Component
public class CaffeineSessionStorage implements SessionStorage {

    private final Cache<String, BasicSession> sessionCaffeine;

    public CaffeineSessionStorage(SolairelightProperties solairelightProperties){
        int idleTime = solairelightProperties.getSession().getIdle();
        sessionCaffeine = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(Duration.ofSeconds(idleTime))
                .scheduler(Scheduler.systemScheduler())
                .removalListener(new SessionRemovalCallback())
                .weakValues()
                .build();
    }

    @Override
    public void put(String key, BasicSession session) {
        this.sessionCaffeine.put(key, session);
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
        return keys.stream().map(key->this.sessionCaffeine.getIfPresent(key)).collect(Collectors.toSet());
    }

    @Override
    public void invalidate(String key) {
        this.sessionCaffeine.invalidate(key);
    }
}
