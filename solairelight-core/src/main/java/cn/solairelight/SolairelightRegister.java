package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.NodeData;
import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.properties.SolairelightProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import javax.annotation.Resource;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements SmartLifecycle {
    private boolean running = false;

    private SolairelightProperties solairelightProperties;

    private ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate;

    @Value("${server.port}")
    private String port;

    public SolairelightRegister(SolairelightProperties solairelightProperties, ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate) {
        this.solairelightProperties = solairelightProperties;
        this.solairelightRedisTemplate = solairelightRedisTemplate;
    }

    @Override
    public void start() {
        ClusterTools.initNodeId(solairelightProperties.getCluster().getNodeIdSuffix());
        NodeData.instance.getBasicInfo().setPort(port);
        SolairelightRedisClient.init(solairelightRedisTemplate).nodeRegister();
        this.running = true;
    }

    @Override
    public void stop() {
        SolairelightRedisClient.getInstance().nodeUnregister();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
