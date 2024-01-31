package io.github.joelou.solairelight.config;

import io.github.joelou.solairelight.gateway.BroadcastGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Joel Ou
 */
@Configuration
@Import(SolairelightCloudConfiguration.class)
public class SolairelightGatewayConfiguration {

    @Bean
    public BroadcastGlobalFilter broadcastGlobalFilter(){
        return new BroadcastGlobalFilter();
    }
}
