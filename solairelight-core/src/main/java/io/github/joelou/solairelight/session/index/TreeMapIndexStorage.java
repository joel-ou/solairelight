package io.github.joelou.solairelight.session.index;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joel Ou
 */
@Primary
@Qualifier("treeMapIndex")
@Repository
public class TreeMapIndexStorage implements IndexStorage {

    private final Map<String, Set<String>> indexes = new TreeMap<>();

    private final List<String> emptyKeys = new ArrayList<>();

    private final float CLEAR_THRESHOLD = 0.3f;

    private final ExecutorService CLEAR_THREAD = Executors.newSingleThreadExecutor();

    @Async
    public void unique(String key, String value){
        index(key, value);
    }

    @Async
    public void index(String key, String value){
        synchronized (indexes) {
            Set<String> indexSet = indexes.computeIfAbsent(key, (k)-> new LinkedHashSet<>());
            indexSet.add(value);
        }
        doClear();
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
        for (Map.Entry<String, Set<String>> entry : indexes.entrySet()) {
            Set<String> set = entry.getValue();
            set.remove(value);
            if(set.isEmpty()) {
                emptyKeys.add(entry.getKey());
            }
        }
        doClear();
    }

    private void doClear(){
        if((float) emptyKeys.size() / indexes.size() > CLEAR_THRESHOLD) {
            CLEAR_THREAD.execute(()->{
                for (String emptyKey : emptyKeys) {
                    Set<String> set = indexes.get(emptyKey);
                    if(!set.isEmpty()) continue;
                    //reduce locker time.
                    synchronized (indexes) {
                        if(set.isEmpty()){
                            indexes.remove(emptyKey);
                        }
                    }
                }
                emptyKeys.clear();
            });
        }
    }
}
