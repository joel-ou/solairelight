package com.github.joelou.solairelight.autoconfig;

import com.github.joelou.solairelight.SolairelightPackage;
import com.github.joelou.solairelight.SolairelightStarter;
import com.github.joelou.solairelight.brodcast.BroadcastRequestFunctionHandler;
import com.github.joelou.solairelight.brodcast.BroadcastService;
import com.github.joelou.solairelight.event.SolairelightEvent;
import com.github.joelou.solairelight.filter.SolairelightFilter;
import com.github.joelou.solairelight.forward.ForwardService;
import com.github.joelou.solairelight.properties.SolairelightProperties;
import com.github.joelou.solairelight.socket.SolairelightWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SolairelightAutoConfig {

    @Bean
    public SolairelightStarter solairelightRegister(SolairelightProperties solairelightProperties,
                                                    @Autowired(required = false) ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                                    Set<SolairelightFilter<?>> filters,
                                                    Set<SolairelightEvent<?>> events){
        return new SolairelightStarter(solairelightProperties, solairelightRedisTemplate,
                filters, events);
    }

    @Bean
    public HandlerMapping handlerMapping(SolairelightProperties properties,
                                         SolairelightWebSocketHandler solairelightWebSocketHandler,
                                         SolairelightStarter solairelightStarter) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(properties.getWebSocketPath(), solairelightWebSocketHandler);
        int order = -1; // before annotated controllers
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
    @ConditionalOnClass(ReactiveRedisTemplate.class)
    @ConditionalOnProperty(value = "solairelight.cluster.enable", havingValue = "true")
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
}
