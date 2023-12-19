package cn.solairelight.brodercast;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Joel Ou
 */
@Service
public class BroadcastService {
    @Resource
    private Map<String, Broadcaster> broadcasters;

    public boolean broadcast(BroadcastParam broadcastParam){
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        return broadcasters
                .get(broadcastChannel.getServiceName())
                .broadcast(broadcastParam);
    }
}
