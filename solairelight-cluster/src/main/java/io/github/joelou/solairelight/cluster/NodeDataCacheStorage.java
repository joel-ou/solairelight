package io.github.joelou.solairelight.cluster;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
class NodeDataCacheStorage {
    private final static Set<NodeDataCache> nodeDataCache = new HashSet<>();

    public static boolean add(NodeData nodeData) {
        Optional<NodeDataCache> nd;
        NodeDataCache cache = new NodeDataCache();
        if((nd = find(nodeData)).isPresent()) {
            cache = nd.get();
            nodeDataCache.remove(cache);
        }
        cache.update(nodeData);
        return nodeDataCache.add(cache);
    }


    public static List<NodeData> getCache(){
        long cur = System.currentTimeMillis();
        AtomicReference<NodeDataCache> del = new AtomicReference<>();
        List<NodeData> result = nodeDataCache.stream()
                .filter(cache->{
                    if(cache.isSleep()) return false;
                    int status = cache.getNodeData().getBasicInfo().getStatus().intValue();
                    long lastHeartbeat;
                    if((lastHeartbeat = cache.getNodeData().getVersion()) != 0
                            && cur- lastHeartbeat>= Duration.ofSeconds(60).toMillis()
                            && status == 1) {
                        unhealthy(cache.getNodeData());
                    }
                    //delete the node after 10 minutes exceeded.
                    if((cur - lastHeartbeat) > Duration.ofMinutes(10).toMillis()
                            && status == 3){
                        del.set(cache);
                        return false;
                    }
                    return status != 3;
                })
                .map(NodeDataCache::getNodeData)
                .collect(Collectors.toList());
        nodeDataCache.remove(del.get());
        return result;
    }

    public static void unhealthy(NodeData nodeData){
        status(nodeData.getBasicInfo(), 1, 2);
    }

    public static void failed(NodeData nodeData){
        status(nodeData.getBasicInfo(), 2, 3);
    }

    public static void unhealthy(NodeData.BasicInfo basicInfo){
        status(basicInfo, 1, 2);
    }

    public static void failed(NodeData.BasicInfo basicInfo){
        find(basicInfo).ifPresent(n->{
            n.failed();
            if(n.getFailedTimes() >= 3) {
                if(basicInfo.getStatus().intValue() == 2) {
                    status(basicInfo, 2, 3);
                } else {
                    n.sleep();
                }
            }
        });
    }

    public static void recover(NodeData.BasicInfo basicInfo) {
        if(basicInfo.getStatus().intValue() == 3)
            status(basicInfo, 3, 2);
    }

    private static void status(NodeData.BasicInfo basicInfo, int expect, int newVal) {
        find(basicInfo).ifPresent(n->{
                    n.getNodeData().getBasicInfo().getStatus().compareAndSet(expect, newVal);
                });
    }

    private static Optional<NodeDataCache> find(NodeData nodeData){
        return nodeDataCache.stream()
                .filter(n->n.getNodeData().equals(nodeData)).findAny();
    }

    private static Optional<NodeDataCache> find(NodeData.BasicInfo basicInfo){
        return nodeDataCache.stream()
                .filter(n->n.getNodeData().getBasicInfo().getNodeId().equals(basicInfo.getNodeId()))
                .findAny();
    }
}
