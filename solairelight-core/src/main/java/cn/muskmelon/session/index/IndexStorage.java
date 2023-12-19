package cn.muskmelon.session.index;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joel Ou
 */
@Repository
public class IndexStorage {

    public enum IndexType {
        UNIQUE,
        RANGE;
    }

    private final Map<String, Set<String>> indexes = new ConcurrentHashMap<>(1024);

    @Async
    public void unique(String key, String value){
        index(key, value);
    }

    @Async
    public void index(String key, String value){
        Set<String> indexSet = indexes.computeIfAbsent(key, (k)-> new LinkedHashSet<>());
        synchronized (this) {
            indexSet.add(value);
        }
    }

    public Set<String> get(String key){
        return indexes.get(key);
    }

    public boolean exist(String key){
        return indexes.containsKey(key);
    }

    public void remove(String key){
        indexes.remove(key);
    }

    public void removeByValue(String value){
        for (Set<String> set : indexes.values()) {
            set.removeIf(val->val.equals(value));
        }
    }
}
