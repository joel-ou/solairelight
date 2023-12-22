package cn.solairelight.cluster;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightRedisClient {

    private final String NODE_REDIS_KEY = "solairelight:nodes";

    private final String ID_STORAGE_REDIS_KEY = "solairelight:idStorage";

    private final NodeData.BasicInfo NODE_INFO = new NodeData.BasicInfo();

    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private ReactiveHashOperations<String, String, Object> hashOperations;

    @Getter
    private static SolairelightRedisClient instance;

    private SolairelightRedisClient(){}

    public static synchronized SolairelightRedisClient init(ReactiveRedisTemplate<String, Object> redisTemplate){
        if(instance != null) return instance;
        SolairelightRedisClient client = new SolairelightRedisClient();
        client.redisTemplate = redisTemplate;
        client.hashOperations = redisTemplate.opsForHash(RedisSerializationContext.string());
        instance = client;
        return instance;
    }

    public void nodeRegister(){
        NODE_INFO.updateVersion();
        String msgPrefix = buildMsg(NODE_INFO);
        redisTemplate
                .opsForSet()
                .add(NODE_REDIS_KEY, NODE_INFO)
                .doOnSuccess(r-> {
                    if(r < 0){
                        log.info("{} register failed. redis returns 0", msgPrefix);
                        throw new RuntimeException("node register failed");
                    } else {
                        log.info("{} register success.", msgPrefix);
                    }
                })
                .doOnError(e-> log.error("{} register failed.", msgPrefix, e))
                .subscribe();
        redisTemplate.expire(NODE_REDIS_KEY, Duration.ofDays(30));
    }

    public void nodeUnregister(){
        String msgPrefix = buildMsg(NODE_INFO);
        redisTemplate
                .opsForSet()
                .remove(NODE_REDIS_KEY, NODE_INFO)
                .doOnSuccess(r-> {
                    if(r < 0){
                        log.info("{} unregister failed. redis returns 0", msgPrefix);
                    } else {
                        log.info("{} unregister success.", msgPrefix);
                    }
                })
                .doOnError(e-> log.error("{} unregister failed.", msgPrefix, e))
                .subscribe();
    }

    public Flux<NodeData.BasicInfo> getNodes(){
        return redisTemplate.opsForSet().members(NODE_REDIS_KEY).map(o->(NodeData.BasicInfo) o);
    }

    public Mono<NodeData.BasicInfo> getNodesById(String id){
        return hashOperations.get(ID_STORAGE_REDIS_KEY, id).map(obj->(NodeData.BasicInfo) obj);
    }

    public void pushId(String... id){
        Map<String, NodeData.BasicInfo> ids = new LinkedHashMap<>(id.length);
        for (String key : id) {
            ids.put(key, NODE_INFO);
        }
        hashOperations.putAll(ID_STORAGE_REDIS_KEY, ids).subscribe();
    }

    public void removeId(Object... id){
        hashOperations.remove(ID_STORAGE_REDIS_KEY, id).subscribe();
    }

    private String buildMsg(NodeData.BasicInfo basicInfo){
        return String.format("node %s ipAddress %s", basicInfo.getNodeId(), basicInfo.getIpAddress());
    }
}
