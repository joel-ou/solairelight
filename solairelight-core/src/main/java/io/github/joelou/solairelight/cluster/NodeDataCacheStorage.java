package io.github.joelou.solairelight.cluster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Joel Ou
 */
class NodeDataCacheStorage {
    private final static Set<NodeDataCache> nodeDataCache = new HashSet<>();

    public static boolean add(NodeData nodeData) {
        Optional<NodeDataCache> nd;
        if((nd = find(nodeData)).isPresent()) {
            int status = nd.get().getNodeData().getBasicInfo().getStatus().intValue();
            //keep the status of the cached node.
            if(status == 3) {
                nodeData.getBasicInfo().getStatus().set(status);
            }
        }
        return nodeDataCache.add(new NodeDataCache(nodeData));
    }


    public static List<NodeData> getCache(){
        long cur = System.currentTimeMillis();
        return nodeDataCache.stream()
                .filter(cache->{
                    if(cur-cache.getNodeData().getLastHeartbeat()<=5000) {
                        status(cache.getNodeData().getBasicInfo(), 1, 2);
                        return false;
                    }
                    return cache.getNodeData().getBasicInfo().getStatus().intValue() != 3;
                })
                .map(NodeDataCache::getNodeData)
                .collect(Collectors.toList());
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
