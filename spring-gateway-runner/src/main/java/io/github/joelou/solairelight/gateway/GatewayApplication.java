package io.github.joelou.solairelight.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Joel Ou
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplicationBuilder()
                .sources(GatewayApplication.class)
                .properties("server.port=9090")
                .web(WebApplicationType.REACTIVE)
                .build();
        springApplication.run().start();
    }
}
