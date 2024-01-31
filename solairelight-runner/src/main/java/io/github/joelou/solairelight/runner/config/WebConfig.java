package io.github.joelou.solairelight.runner.config;

import io.github.joelou.solairelight.config.SolairelightCloudConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Joel Ou
 */
@Configuration
@Import(SolairelightCloudConfiguration.class)
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    }
}
