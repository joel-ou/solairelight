package io.github.joelou.solairelight.event;

import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.session.BasicSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Stream;

/**
 * use to share unique index among the nodes of cluster.
 * @author Joel Ou
 */
@Service
public class UniqueIndexEvent implements GlobalEvent {
    @Resource
    private SolairelightProperties properties;

    @Override
    public void execute(EventContext<Object> context) {
        SolairelightRedisClient solairelightRedisClient = SolairelightRedisClient.getInstance();
        BasicSession basicSession;
        switch (context.getTrigger()) {
            case SESSION_CONNECTED:
                basicSession = toSession(context.getArgument());
                Object id = basicSession.getUserMetadata().getUserFeatures().get("id");
                if(id != null)
                    solairelightRedisClient.pushId(id.toString());
                break;
            case SESSION_DISCONNECTED:
                basicSession = toSession(context.getArgument());
                Object removeId = basicSession.getUserMetadata().getUserFeatures().get("id");
                if(removeId != null)
                    solairelightRedisClient.removeId(removeId.toString());
                break;
            default:
                break;
        }
    }

    private BasicSession toSession(Object session){
        return ((BasicSession) session);
    }

    private Stream<Map.Entry<String, Object>> keys(Map<String, Object> userFeatures){
        return userFeatures.entrySet().stream().filter(entry-> entry.getKey().equals("id"));
    }
}
