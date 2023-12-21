package cn.solairelight;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.cluster.ZookeeperExecutor;
import cn.solairelight.properties.SolairelightProperties;
import org.springframework.context.SmartLifecycle;

import javax.annotation.Resource;

/**
 * @author Joel Ou
 */
public class SolairelightRegister implements SmartLifecycle {
    private boolean running = false;

    @Resource
    private SolairelightProperties solairelightProperties;

    public SolairelightRegister(){
        System.out.println();
    }

    @Override
    public void start() {
        ClusterTools.getNodeId();
        ZookeeperExecutor.init(solairelightProperties.getZookeeper());
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
