package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.NodeData;
import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.filter.Filter;
import cn.solairelight.filter.factory.FilterFactory;
import cn.solairelight.properties.SolairelightProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements SmartLifecycle {
    private boolean running = false;

    private SolairelightProperties solairelightProperties;

    private ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate;

    private Set<Filter<?>> filters;

    @Value("${server.port}")
    private String port;

    public SolairelightRegister(SolairelightProperties solairelightProperties,
                                ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                Set<Filter<?>> filters) {
        this.solairelightProperties = solairelightProperties;
        this.solairelightRedisTemplate = solairelightRedisTemplate;
        this.filters = filters;
    }

    @Override
    public void start() {
        ClusterTools.initNodeId(solairelightProperties.getCluster().getNodeIdSuffix());
        NodeData.instance.getBasicInfo().setPort(port);
        SolairelightRedisClient.init(solairelightRedisTemplate).nodeRegister();
        FilterFactory.init(this.filters);
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
