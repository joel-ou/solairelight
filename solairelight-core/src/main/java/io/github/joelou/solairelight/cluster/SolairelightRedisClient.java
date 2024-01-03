package io.github.joelou.solairelight.cluster;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightRedisClient {

    private final String NODE_REDIS_KEY = "solairelight:nodes";

    private final String ID_STORAGE_REDIS_KEY = "solairelight:idStorage";

    private final NodeData.BasicInfo NODE_INFO = NodeData.instance.getBasicInfo();

    private ReactiveRedisTemplate<Object, Object> redisTemplate;

    private ReactiveHashOperations<Object, Object, Object> hashOperations;

    @Getter
    private static SolairelightRedisClient instance;

    private String nodeId;

    private boolean running = false;

    private volatile List<NodeData> nodeDataCache = Collections.emptyList();

    private SolairelightRedisClient(){}

    public static synchronized SolairelightRedisClient init(ReactiveRedisTemplate<Object, Object> redisTemplate){
        if(instance != null) return instance;
        SolairelightRedisClient client = new SolairelightRedisClient();
        client.redisTemplate = redisTemplate;
        client.hashOperations = redisTemplate.opsForHash();
        instance = client;
        return instance;
    }

    public void nodeRegister(){
        nodeRegister(false);
    }

    public void nodeRegister(boolean reRegister){
        running = true;
        nodeId = NODE_INFO.getNodeId();
        NODE_INFO.updateVersion();
        String msgPrefix = buildMsg(NODE_INFO);
        Mono<Boolean> mono = redisTemplate
                .opsForValue()
                .set(buildNodeRedisKey(), NodeData.instance, Duration.ofSeconds(30))
                .doOnSuccess(r-> {
                    if(!r){
                        log.info("{} register failed. redis returns 0", msgPrefix);
                        throw new RuntimeException("node register failed");
                    } else if(!reRegister){
                        log.info("{} register success.", msgPrefix);
                    }
                })
                .doOnError(e-> log.error("{} register failed.", msgPrefix, e));
        if(reRegister){
            mono = mono.retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).doAfterRetry(signal->{
                log.error("try register too many times. node failed. {}", signal);
                Runtime.getRuntime().exit(1);
            }));
        }
        mono.subscribe();
        if(!reRegister) heartbeat();
    }

    public void nodeUnregister(){
        running = false;
        String msgPrefix = buildMsg(NODE_INFO);
        redisTemplate
                .opsForValue()
                .delete(buildNodeRedisKey())
                .doOnSuccess(r-> {
                    if(!r){
                        log.info("{} unregister failed. redis returns 0", msgPrefix);
                    } else {
                        log.info("{} unregister success.", msgPrefix);
                    }
                })
                .doOnError(e-> log.error("{} unregister failed.", msgPrefix, e))
                .subscribe();
    }

    public List<NodeData> getNodeCache(){
        return nodeDataCache;
    }

    public Flux<NodeData> getNodes(){
        return getNodes(false);
    }

    public Flux<NodeData> getNodes(boolean isRefresh){
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(NODE_REDIS_KEY+":*")
                .build();
        return redisTemplate.scan(scanOptions)
                .collectList()
                .flatMapMany(keys->{
                    if(!isRefresh)
                        log.debug("found nodes: {}", keys);
                    if(CollectionUtils.isEmpty(keys)) return Flux.empty();
                    return redisTemplate
                            .opsForValue()
                            .multiGet(keys)
                            .flatMapMany(Flux::fromIterable)
                            .cast(NodeData.class);
                });
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

    public void heartbeat(){
        Thread heartbeat = new Thread(()->{
            while (running) {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(!running) return;
                //do re-register for update node data.
                nodeRegister(true);
                //refresh node cache.
                getNodes(true)
                        .filter(nodeData -> !nodeData.getBasicInfo().getNodeId().equals(NODE_INFO.getNodeId()))
                        .collectList().subscribe(list->nodeDataCache=list);
            }
        });
        heartbeat.setName("Solairelight-Heartbeat-Thread"+ClusterTools.getNodeId());
        heartbeat.start();
    }

    private String buildMsg(NodeData.BasicInfo basicInfo){
        return String.format("node %s ipAddress %s", basicInfo.getNodeId(), basicInfo.getIpAddress());
    }

    private String buildNodeRedisKey(){
        return NODE_REDIS_KEY+":"+nodeId;
    }
}
