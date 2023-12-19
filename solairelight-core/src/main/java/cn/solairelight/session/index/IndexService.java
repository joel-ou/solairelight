package cn.solairelight.session.index;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Service
public class IndexService {
    @Resource
    private IndexStorage indexStorage;

    public void index(String key, String value, String sessionId){
        indexStorage.index(path(key, value), sessionId);
    }

    public void unique(String key, String value, String sessionId){
        indexStorage.unique(path(key, value), sessionId);
    }

    @Nullable
    public String get(String key, String value){
        Set<String> indexes = indexStorage.get(path(key, value));
        if(indexes == null) return null;
        return indexes.stream().findFirst().orElse(null);
    }

    @Nullable
    public Set<String> getAll(String key, String value){
        return indexStorage.get(path(key, value));
    }

    public void delete(String path){
        indexStorage.remove(path);
    }

    public void deleteBySessionId(String sessionId){
        indexStorage.removeByValue(sessionId);
    }

    private String path(String key, String value){
        return key+":"+value;
    }
}
