package cn.solairelight.config;

import cn.solairelight.SolairelightPackage;
import cn.solairelight.brodercast.BroadcastRequestFunctionHandler;
import cn.solairelight.brodercast.BroadcastService;
import cn.solairelight.event.EventContext;
import cn.solairelight.event.EventTrigger;
import cn.solairelight.filter.chain.InboundMessageFilterChain;
import cn.solairelight.filter.chain.OutboundMessageFilterChain;
import cn.solairelight.filter.chain.SessionFilterChain;
import cn.solairelight.filter.message.MessageFilter;
import cn.solairelight.filter.session.SessionFilter;
import cn.solairelight.properties.SolairelightProperties;
import cn.solairelight.session.BasicSession;
import cn.solairelight.session.SessionRemovalCallback;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Ou
 */
@AutoConfiguration
@ComponentScan(basePackageClasses= SolairelightPackage.class)
@Slf4j
public class SolairelightAutoConfig {

    //chains
    @Bean
    public SessionFilterChain sessionFilters(Set<SessionFilter> sessionFilters){
        return new SessionFilterChain(sessionFilters);
    }

    @Bean
    public InboundMessageFilterChain inboundMessageFilters(Set<MessageFilter> inboundMessageFilterChain){
        return new InboundMessageFilterChain(inboundMessageFilterChain);
    }

    @Bean
    public OutboundMessageFilterChain outboundMessageFilters(Set<SessionFilter> outboundMessageFilterChain){
        return new OutboundMessageFilterChain(outboundMessageFilterChain);
    }

    //event triggers
    @Bean
    public Map<EventContext.EventType, EventTrigger> eventTriggers(){
        EventContext.EventType[] eventTypes = EventContext.EventType.values();
        Map<EventContext.EventType, EventTrigger> eventTriggers = new HashMap<>(eventTypes.length);
        for (EventContext.EventType evenType : eventTypes) {
            eventTriggers.put(evenType, EventTrigger.create(evenType));
        }
        return eventTriggers;
    }

    @Bean
    public Cache<String, BasicSession> sessionCaffeine(SolairelightProperties solairelightProperties){
        int idleTime = solairelightProperties.getSession().getIdle();
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(Duration.ofSeconds(idleTime))
                .scheduler(Scheduler.systemScheduler())
                .removalListener(new SessionRemovalCallback())
                .weakValues()
                .build();
    }

    @Bean
    public HandlerMapping handlerMapping(SolairelightProperties properties) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(properties.getWebSocketPath(), muskmelonWebSocketHandler());
        int order = -1; // before annotated controllers

        log.info("muskmelon websocket stared. path: {}", properties.getWebSocketPath());
        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public RouterFunction<ServerResponse> broadcastFunction(BroadcastService broadcastService){
        return BroadcastRequestFunctionHandler.broadcast(broadcastService);
    }

    @Bean
    public SolairelightWebSocketHandler muskmelonWebSocketHandler(){
        return new SolairelightWebSocketHandler();
    }
}
