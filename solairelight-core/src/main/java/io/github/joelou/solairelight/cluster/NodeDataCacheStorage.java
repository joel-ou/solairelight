package io.github.joelou.solairelight.cluster;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
class NodeDataCacheStorage {
    private final static Set<NodeDataCache> nodeDataCache = new HashSet<>();

    public static boolean add(NodeData nodeData) {
        Optional<NodeDataCache> nd;
        NodeDataCache cache = new NodeDataCache(nodeData);
        if((nd = find(nodeData)).isPresent()) {
            int status = nd.get().getNodeData().getBasicInfo().getStatus().intValue();
            //keep the status of the cached node.
            if(status == 3 && nodeData.getVersion() > 0) {
                nodeData.getBasicInfo().getStatus().set(status);
            }
            nodeDataCache.remove(cache);
        }
        return nodeDataCache.add(cache);
    }


    public static List<NodeData> getCache(){
        long cur = System.currentTimeMillis();
        AtomicReference<NodeDataCache> del = new AtomicReference<>();
        List<NodeData> result = nodeDataCache.stream()
                .filter(cache->{
                    int status = cache.getNodeData().getBasicInfo().getStatus().intValue();
                    long lastHeartbeat;
                    if((lastHeartbeat = cache.getNodeData().getVersion()) != 0
                            && cur- lastHeartbeat>= Duration.ofSeconds(5).toMinutes()
                            && status == 1) {
                        status(cache.getNodeData().getBasicInfo(), 1, 2);
                    }
                    //delete the node after 10 minutes exceeded.
                    if((cur - lastHeartbeat) > Duration.ofMinutes(1).toMinutes()
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
            if(n.failedTimes() >= 3) {
                status(basicInfo, 2, 3);
            } else {
                n.failed();
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
