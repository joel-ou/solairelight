package io.github.joelou.solairelight;

import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import io.github.joelou.solairelight.event.SolairelightEvent;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.util.Set;

/**
 * @author Joel Ou
 */
@Slf4j
public class ClusterSolairelightStarter extends SolairelightStarter {

    private final ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate;

    public ClusterSolairelightStarter(SolairelightProperties solairelightProperties,
                                      ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                      Set<SolairelightFilter<?>> filters,
                                      Set<SolairelightEvent<?>> events) {
        super(solairelightProperties, filters, events);
        this.solairelightRedisTemplate = solairelightRedisTemplate;
    }

    @Override
    public void start() {
        super.start();
        //cluster node register.
        SolairelightRedisClient.init(solairelightRedisTemplate).nodeRegister();
    }

    @Override
    public void stop() {
        super.stop();
        SolairelightRedisClient.getInstance().nodeUnregister();
    }
}
