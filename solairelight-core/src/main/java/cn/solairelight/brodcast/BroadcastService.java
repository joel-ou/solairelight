package cn.solairelight.brodcast;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Service
public class BroadcastService {
    @Resource
    private Map<String, Broadcaster> broadcasters;

    public void broadcast(BroadcastParam broadcastParam){
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        broadcasters
                .get(broadcastChannel.getServiceName())
                .broadcast(broadcastParam);
    }

    public void distributorEntrance(BroadcastParam broadcastParam){
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        broadcasters
                .get(broadcastChannel.getServiceName())
                .localBroadcast(broadcastParam);
    }
}
