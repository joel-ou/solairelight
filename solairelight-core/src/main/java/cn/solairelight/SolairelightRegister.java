package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import org.springframework.context.Lifecycle;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements Lifecycle {
    private boolean running = false;

    @Override
    public void start() {
        ClusterTools.generateNodeId();
        this.running = true;
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
