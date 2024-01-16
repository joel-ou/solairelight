package io.github.joelou.solairelight.autoconfig;

import io.github.joelou.solairelight.cluster.ClusterSolairelightStarter;
import io.github.joelou.solairelight.SolairelightStarter;
import io.github.joelou.solairelight.brodcast.BroadcastRequestFunctionHandler;
import io.github.joelou.solairelight.brodcast.BroadcastService;
import io.github.joelou.solairelight.event.SolairelightEvent;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Set;

/**
 * @author Joel Ou
 */
@AutoConfiguration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Import(SolairelightConfig.class)
@ConditionalOnProperty(value = "solairelight.cluster.enable", havingValue = "true")
@ConditionalOnClass(value={ReactiveRedisTemplate.class, ReactiveRedisConnectionFactory.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ClusterSolairelightAutoConfig {

    @Bean
    public SolairelightStarter solairelightStarter(SolairelightProperties solairelightProperties,
                                                    ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate,
                                                    Set<SolairelightFilter<?>> filters,
                                                    Set<SolairelightEvent<?>> events){
        return new ClusterSolairelightStarter(solairelightProperties, solairelightRedisTemplate,
                filters, events);
    }

    //functions
    @Bean
    public RouterFunction<ServerResponse> distributorEntrance(BroadcastService broadcastService){
        return BroadcastRequestFunctionHandler.distributorEntrance(broadcastService);
    }

    @Bean
    public ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
                                                                           ResourceLoader resourceLoader) {
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
