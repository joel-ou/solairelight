package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.properties.SolairelightProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import javax.annotation.Resource;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements SmartLifecycle {
    private boolean running = false;

    @Resource
    private SolairelightProperties solairelightProperties;

    @Resource
    private ReactiveRedisTemplate<String, Object> solairelightRedisTemplate;

    @Override
    public void start() {
        ClusterTools.getNodeId();
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
