package io.github.joelou.solairelight.autoconfig;

import io.github.joelou.solairelight.SolairelightStarter;
import io.github.joelou.solairelight.event.SolairelightEvent;
import io.github.joelou.solairelight.filter.SolairelightFilter;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

/**
 * @author Joel Ou
 */
@AutoConfiguration
@Import(SolairelightConfig.class)
@ConditionalOnProperty(value = "solairelight.cluster.enable", havingValue = "false")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class StandaloneSolairelightAutoConfig {

    @Bean
    public SolairelightStarter solairelightStarter(SolairelightProperties solairelightProperties,
                                                    Set<SolairelightFilter<?>> filters,
                                                    Set<SolairelightEvent<?>> events,
                                                   WebClient.Builder loadBalancedWebClientBuilder){
        return new SolairelightStarter(solairelightProperties,
                filters, events, loadBalancedWebClientBuilder);
    }
}
