package io.github.joelou.solairelight;

import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.event.EventFactory;
import io.github.joelou.solairelight.event.SolairelightEvent;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.filter.factory.FilterFactory;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.session.SessionBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightStarter implements Lifecycle {
    private boolean running = false;

    protected final SolairelightProperties solairelightProperties;

    private final Set<SolairelightFilter<?>> filters;

    private final Set<SolairelightEvent<?>> events;

    @Value("${server.port}")
    private String port;

    public SolairelightStarter(SolairelightProperties solairelightProperties,
                               Set<SolairelightFilter<?>> filters,
                               Set<SolairelightEvent<?>> events) {
        this.solairelightProperties = solairelightProperties;
        this.filters = filters;
        this.events = events;
    }

    @Override
    public void start() {
        //init properties
        SolairelightSettings.setCluster(solairelightProperties.getCluster().isEnable());
        SolairelightSettings.setNodeIdSuffix(solairelightProperties.getCluster().getNodeIdSuffix());
        ClusterTools.initNodeId(solairelightProperties.getCluster().getNodeIdSuffix());
        NodeData.instance.getBasicInfo().setPort(port);
        NodeData.instance.getSessionNumber().set(solairelightProperties.getSession().getMaxNumber());
        //init components
        SessionBroker.init(solairelightProperties);
        FilterFactory.init(this.filters);
        EventFactory.init(events);
        //after finished
        this.running = true;
        log.info("solairelight started on path {} mode: {}", solairelightProperties.getWebSocketPath(),
                SolairelightSettings.isCluster()?"cluster":"standalone");
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
