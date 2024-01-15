package io.github.joelou.solairelight.cluster;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
@Slf4j
public class NodeDataCache {
    @Getter
    private NodeData nodeData;

    private final AtomicInteger failedTimes = new AtomicInteger(0);

    private long timestamp = 0L;

    private boolean sleep = false;

    public NodeDataCache() {
    }
    public NodeDataCache(NodeData nodeData) {
        this.nodeData = nodeData;
    }

    public void failed(){
        if(timestamp > 0 && (System.currentTimeMillis()-timestamp) > Duration.ofMinutes(5).toMillis()) {
            failedTimes.set(1);
        } else {
            timestamp = System.currentTimeMillis();
            failedTimes.incrementAndGet();
        }
    }

    public int getFailedTimes(){
        return failedTimes.intValue();
    }

    public boolean isSleep() {
        if(sleep && System.currentTimeMillis() - timestamp > Duration.ofMinutes(3).toMillis())
            wakeup();
        return sleep;
    }

    public void sleep() {
        this.sleep = true;
    }

    public void wakeup() {
        this.sleep = false;
    }

    void update(NodeData nodeData) {
        this.nodeData = nodeData;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof NodeDataCache) {
            NodeDataCache nodeDataCache = ((NodeDataCache) object);
            return getNodeData().equals(nodeDataCache.getNodeData());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getNodeData().hashCode();
    }
}
