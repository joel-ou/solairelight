package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.SolairelightSettings;
import io.github.joelou.solairelight.cluster.DistributeResult;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Joel Ou
 */
@Service
public class BroadcastService {
    @Resource
    private Map<String, Broadcaster> broadcasters;

    public Flux<DistributeResult> broadcast(BroadcastParam broadcastParam){
        if(!StringUtils.hasText(broadcastParam.getId())){
            throw new ResponseMessageException("message id can not be null");
        }
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        if(SolairelightSettings.isCluster()) {
            return broadcasters
                    .get(broadcastChannel.getServiceName())
                    .broadcast(broadcastParam);
        } else {
            return broadcasters
                    .get(broadcastChannel.getServiceName())
                    .localBroadcast(broadcastParam)
                    .flux();
        }
    }

    public void distributorEntrance(BroadcastParam broadcastParam){
        String channel = broadcastParam.getChannel().toUpperCase();
        Broadcaster.BroadcastChannel broadcastChannel = Broadcaster.BroadcastChannel.valueOf(channel);
        broadcasters
                .get(broadcastChannel.getServiceName())
                .localBroadcast(broadcastParam);
    }
}
