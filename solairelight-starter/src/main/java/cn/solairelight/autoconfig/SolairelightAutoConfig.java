package cn.solairelight.autoconfig;

import cn.solairelight.SolairelightPackage;
import cn.solairelight.SolairelightRegister;
import cn.solairelight.SolairelightWebSocketHandler;
import cn.solairelight.brodcast.BroadcastRequestFunctionHandler;
import cn.solairelight.brodcast.BroadcastService;
import cn.solairelight.event.SolairelightEvent;
import cn.solairelight.filter.SolairelightFilter;
import cn.solairelight.forward.ForwardService;
import cn.solairelight.properties.SolairelightProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Ou
 */
@AutoConfiguration
@ComponentScan(basePackageClasses= SolairelightPackage.class)
@ConditionalOnProperty(value = "solairelight.enable", havingValue = "true")
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class SolairelightAutoConfig {

    @Bean
    public HandlerMapping handlerMapping(SolairelightProperties properties,
                                         SolairelightWebSocketHandler solairelightWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(properties.getWebSocketPath(), solairelightWebSocketHandler);
        int order = -1; // before annotated controllers

        log.info("solairelight websocket stared. path: {}", properties.getWebSocketPath());
        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public SolairelightWebSocketHandler solairelightWebSocketHandler(ForwardService forwardService){
        return new SolairelightWebSocketHandler(forwardService);
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
                                                     ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                                     Set<SolairelightFilter<?>> filters,
                                                     Set<SolairelightEvent<?>> events){
        return new SolairelightRegister(solairelightProperties, solairelightRedisTemplate,
                filters, events);
    }
}
