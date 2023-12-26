package cn.solairelight.config;

import cn.solairelight.SolairelightPackage;
import cn.solairelight.SolairelightRegister;
import cn.solairelight.brodcast.BroadcastRequestFunctionHandler;
import cn.solairelight.brodcast.BroadcastService;
import cn.solairelight.event.EventContext;
import cn.solairelight.event.EventTrigger;
import cn.solairelight.filter.chain.IncomingMessageFilterChain;
import cn.solairelight.filter.chain.OutgoingMessageFilterChain;
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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
@ConditionalOnProperty(value = "solairelight.enable", havingValue = "true")
@Slf4j
public class SolairelightAutoConfig {

    //chains
    @Bean
    public SessionFilterChain sessionFilters(Set<SessionFilter> sessionFilters){
        return new SessionFilterChain(sessionFilters);
    }

    @Bean
    public IncomingMessageFilterChain inboundMessageFilters(Set<MessageFilter> inboundMessageFilterChain){
        return new IncomingMessageFilterChain(inboundMessageFilterChain);
    }

    @Bean
    public OutgoingMessageFilterChain outboundMessageFilters(Set<SessionFilter> outboundMessageFilterChain){
        return new OutgoingMessageFilterChain(outboundMessageFilterChain);
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
    public HandlerMapping handlerMapping(SolairelightProperties properties) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(properties.getWebSocketPath(), solairelightWebSocketHandler());
        int order = -1; // before annotated controllers

        log.info("solairelight websocket stared. path: {}", properties.getWebSocketPath());
        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public SolairelightWebSocketHandler solairelightWebSocketHandler(){
        return new SolairelightWebSocketHandler();
    }

    //functions
    @Bean
    public RouterFunction<ServerResponse> broadcastFunction(BroadcastService broadcastService){
        return BroadcastRequestFunctionHandler.broadcast(broadcastService);
    }

    @Bean
    @ConditionalOnProperty(value = "solairelight.cluster.enable", havingValue = "true")
    public RouterFunction<ServerResponse> distributorEntrance(BroadcastService broadcastService){
        return BroadcastRequestFunctionHandler.distributorEntrance(broadcastService);
    }

    @Bean
    public ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
        RedisSerializer<String> keySerializer = RedisSerializer.string();
        RedisSerializer<Object> valueSerializer = RedisSerializer.java(resourceLoader.getClassLoader());
        StringRedisSerializer serializer = StringRedisSerializer.UTF_8;
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }

    @Bean
    @ConditionalOnClass(ReactiveRedisTemplate.class)
    @ConditionalOnProperty(value = "solairelight.cluster.enable", havingValue = "true")
    public SolairelightRegister solairelightRegister(SolairelightProperties solairelightProperties,
                                                     ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate){
        return new SolairelightRegister(solairelightProperties, solairelightRedisTemplate);
    }
}
