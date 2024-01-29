package io.github.joelou.solairelight.cloud;

import io.github.joelou.solairelight.config.SolairelightGatewayConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Joel Ou
 */
@Configuration
@Import(SolairelightGatewayConfiguration.class)
public class GatewayConfig {
}
