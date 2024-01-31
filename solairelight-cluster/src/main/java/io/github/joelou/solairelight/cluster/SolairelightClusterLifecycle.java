package io.github.joelou.solairelight.cluster;

import org.springframework.context.Lifecycle;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @author Joel Ou
 */
public class SolairelightClusterLifecycle implements Lifecycle, Ordered {

    private boolean isRunning = false;

    public SolairelightClusterLifecycle(ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate) {
        SolairelightRedisClient.init(solairelightRedisTemplate);
    }

    @Override
    public void start() {
        isRunning = true;
        SolairelightRedisClient
                .getInstance()
                .startNodeSync(() -> !isRunning).subscribe();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE-1;
    }
}
