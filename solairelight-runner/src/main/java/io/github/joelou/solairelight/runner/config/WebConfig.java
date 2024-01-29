package io.github.joelou.solairelight.runner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Joel Ou
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/solairelight/**")
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET")
                .allowCredentials(true).maxAge(3600);

        registry.addMapping("/session/**")
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET")
                .allowCredentials(true).maxAge(3600);
    }
}
