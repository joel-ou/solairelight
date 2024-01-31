package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.cluster.NodeBroadcastingResponse;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Service
public class BroadcastService {
    @Resource
    private Map<String, Broadcaster> broadcasters;

    public Mono<NodeBroadcastingResponse> broadcast(BroadcastParam broadcastParam){
        if(!StringUtils.hasText(broadcastParam.getId())){
            throw new ResponseMessageException("message id can not be empty");
        }
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        return broadcasters
                .get(broadcastChannel.getServiceName())
                .localBroadcast(broadcastParam);
    }

    public void distributorEntrance(BroadcastParam broadcastParam){
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        broadcasters
                .get(broadcastChannel.getServiceName())
                .localBroadcast(broadcastParam);
    }
}
