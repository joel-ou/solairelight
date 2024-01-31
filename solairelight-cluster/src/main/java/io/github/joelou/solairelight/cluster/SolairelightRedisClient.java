package io.github.joelou.solairelight.cluster;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightRedisClient {

    private final String NODE_REDIS_KEY = "solairelight:nodes";

    private final String ID_STORAGE_REDIS_KEY = "solairelight:idStorage";

    private NodeData.BasicInfo NODE_INFO;

    private ReactiveRedisTemplate<Object, Object> redisTemplate;

    private ReactiveHashOperations<Object, Object, Object> hashOperations;

    @Getter
    private static SolairelightRedisClient instance;

    private String nodeId;

    private boolean running = false;

    private String msgPrefix;

    private SolairelightRedisClient(){}

    public static synchronized SolairelightRedisClient init(ReactiveRedisTemplate<Object, Object> redisTemplate){
        if(instance != null) return instance;
        SolairelightRedisClient client = new SolairelightRedisClient();
        client.redisTemplate = redisTemplate;
        client.hashOperations = redisTemplate.opsForHash();
        instance = client;
        return instance;
    }

    void nodeRegister(){
        running = true;
        NODE_INFO = NodeData.instance.getBasicInfo();
        nodeId = NODE_INFO.getNodeId();
        msgPrefix = buildMsg(NODE_INFO);
        nodeRegister(false);
    }

    void nodeRegister(boolean reRegister){
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

    void nodeUnregister(){
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
        return NodeDataCacheStorage.getCache();
    }

    public Flux<NodeData> getNodeCacheFlux(){
        return Flux.fromIterable(NodeDataCacheStorage.getCache());
    }

    public Flux<NodeData> getNodes(){
        return getNodes(false);
    }

    public Flux<NodeData> getNodes(boolean isRefreshCache){
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(NODE_REDIS_KEY+":*")
                .build();
        return redisTemplate.scan(scanOptions)
                .collectList()
                .flatMapMany(keys->{
                    if(!isRefreshCache)
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
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(!running) return;
                NodeData.instance.updateVersion();
                //do re-register for update node data.
                nodeRegister(true);
            }
        });
        heartbeat.setName("Solairelight-Heartbeat-Thread"+ClusterTools.getNodeId());
        heartbeat.start();
    }

    public Flux<NodeData> startNodeSync(BooleanSupplier stop){
        log.info("solairelight node-synchronizer started");
        Scheduler scheduler = Schedulers.newSingle("solairelight-node-synchronizer");
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(1), scheduler)
                .handle((el,sink)->{
                    if(stop.getAsBoolean()) {
                        sink.complete();
                        log.info("solairelight node-synchronizer stopped");
                    } else {
                        sink.next(el);
                    }
                })
                .switchMap(l-> refreshCache());
    }

    private Flux<NodeData> refreshCache(){
        //refresh node cache.
        return getNodes(true)
                .doOnNext(NodeDataCacheStorage::add);
    }

    private String buildMsg(NodeData.BasicInfo basicInfo){
        return String.format("node %s ipAddress %s", basicInfo.getNodeId(), basicInfo.getIpAddress());
    }

    private String buildNodeRedisKey(){
        return NODE_REDIS_KEY+":"+nodeId;
    }
}
