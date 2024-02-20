package io.github.joelou.solairelight;

import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.event.EventFactory;
import io.github.joelou.solairelight.event.SolairelightEvent;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.filter.factory.FilterFactory;
import io.github.joelou.solairelight.forward.ForwardWebClient;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.session.SessionBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightStarter implements Lifecycle, Ordered {
    private boolean running = false;

    protected final SolairelightProperties solairelightProperties;

    private final Set<SolairelightFilter<?>> filters;

    private final Set<SolairelightEvent<?>> events;

    @Value("${server.port}")
    private String port;

    private final WebClient.Builder loadBalancedWebClientBuilder;

    public SolairelightStarter(SolairelightProperties solairelightProperties,
                               Set<SolairelightFilter<?>> filters,
                               Set<SolairelightEvent<?>> events,
                               WebClient.Builder loadBalancedWebClientBuilder) {
        this.solairelightProperties = solairelightProperties;
        this.filters = filters;
        this.events = events;
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
    }

    @Override
    public void start() {
        //init properties
        SolairelightSettings.setCluster(solairelightProperties.getCluster().isEnable());
        SolairelightSettings.setNodeIdSuffix(solairelightProperties.getCluster().getNodeIdSuffix());
        ClusterTools.initNodeId(solairelightProperties.getCluster().getNodeIdSuffix());
        NodeData.instance.getBasicInfo().setPort(port);
        NodeData.instance.getSessionQuota().set(solairelightProperties.getSession().getMaxNumber());
        NodeData.instance.setMaxSessionNumber(solairelightProperties.getSession().getMaxNumber());
        //init components
        SessionBroker.init(solairelightProperties);
        FilterFactory.init(this.filters);
        EventFactory.init(events);
        ForwardWebClient.init(loadBalancedWebClientBuilder);
        //after finished
        this.running = true;
        log.info("solairelight started on path {} mode: {}", solairelightProperties.getWebsocket().getPath(),
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

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE-2;
    }
}
