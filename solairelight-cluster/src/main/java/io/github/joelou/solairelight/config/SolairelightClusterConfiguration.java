package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.cluster.SolairelightClusterLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author Joel Ou
 */
@Configuration
@ComponentScan(basePackages = "io.github.joelou.solairelight.cluster")
public class SolairelightClusterConfiguration {

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

    @Bean
    public SolairelightClusterLifecycle clusterLifecycle(ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate){
        return new SolairelightClusterLifecycle(solairelightRedisTemplate);
    }
}
