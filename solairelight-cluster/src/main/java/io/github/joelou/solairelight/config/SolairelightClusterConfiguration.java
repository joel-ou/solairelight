package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.cluster.SolairelightRedisClient;
import org.springframework.context.annotation.Bean;
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
        ReactiveRedisTemplate<Object, Object> solairelightRedisTemplate = new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
        SolairelightRedisClient.init(solairelightRedisTemplate);
        return solairelightRedisTemplate;
    }
}
