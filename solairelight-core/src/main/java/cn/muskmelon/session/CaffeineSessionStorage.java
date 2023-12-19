package cn.muskmelon.session;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
@Component
public class CaffeineSessionStorage implements SessionStorage {

    @Resource
    private Cache<String, BasicSession> sessionCaffeine;

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
