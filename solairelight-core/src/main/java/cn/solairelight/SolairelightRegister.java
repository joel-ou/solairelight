package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.NodeData;
import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.event.EventFactory;
import cn.solairelight.event.SolairelightEvent;
import cn.solairelight.filter.SolairelightFilter;
import cn.solairelight.filter.factory.FilterFactory;
import cn.solairelight.properties.SolairelightProperties;
import cn.solairelight.session.SessionBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements SmartLifecycle {
    private boolean running = false;

    private final SolairelightProperties solairelightProperties;

    private final ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate;

    private final Set<SolairelightFilter<?>> filters;

    private final Set<SolairelightEvent<?>> events;

    @Value("${server.port}")
    private String port;

    public SolairelightRegister(SolairelightProperties solairelightProperties,
                                ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                Set<SolairelightFilter<?>> filters,
                                Set<SolairelightEvent<?>> events) {
        this.solairelightProperties = solairelightProperties;
        this.solairelightRedisTemplate = solairelightRedisTemplate;
        this.filters = filters;
        this.events = events;
    }

    @Override
    public void start() {
        //init properties
        ClusterTools.initNodeId(solairelightProperties.getCluster().getNodeIdSuffix());
        NodeData.instance.getBasicInfo().setPort(port);
        //init components
        SessionBroker.init(solairelightProperties);
        SolairelightRedisClient.init(solairelightRedisTemplate).nodeRegister();
        FilterFactory.init(this.filters);
        EventFactory.init(events);
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
