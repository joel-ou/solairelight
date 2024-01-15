package io.github.joelou.solairelight.cluster;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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

    public int failedTimes(){
        return failedTimes.intValue();
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
