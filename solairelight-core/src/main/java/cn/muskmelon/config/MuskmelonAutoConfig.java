package cn.muskmelon.config;

import cn.muskmelon.MuskmelonPackage;
import cn.muskmelon.brodercast.BroadcastRequestFunctionHandler;
import cn.muskmelon.brodercast.BroadcastService;
import cn.muskmelon.event.EventContext;
import cn.muskmelon.event.EventTrigger;
import cn.muskmelon.filter.chain.InboundMessageFilterChain;
import cn.muskmelon.filter.chain.OutboundMessageFilterChain;
import cn.muskmelon.filter.chain.SessionFilterChain;
import cn.muskmelon.filter.message.MessageFilter;
import cn.muskmelon.filter.session.SessionFilter;
import cn.muskmelon.properties.MuskmelonProperties;
import cn.muskmelon.session.BasicSession;
import cn.muskmelon.session.SessionRemovalCallback;
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
@ComponentScan(basePackageClasses=MuskmelonPackage.class)
@Slf4j
public class MuskmelonAutoConfig {

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
    public Cache<String, BasicSession> sessionCaffeine(MuskmelonProperties muskmelonProperties){
        int idleTime = muskmelonProperties.getSession().getIdle();
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(Duration.ofSeconds(idleTime))
                .scheduler(Scheduler.systemScheduler())
                .removalListener(new SessionRemovalCallback())
                .weakValues()
                .build();
    }

    @Bean
    public HandlerMapping handlerMapping(MuskmelonProperties properties) {
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
    public MuskmelonWebSocketHandler muskmelonWebSocketHandler(){
        return new MuskmelonWebSocketHandler();
    }
}
