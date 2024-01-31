package io.github.joelou.solairelight.autoconfig;

import io.github.joelou.solairelight.SolairelightPackage;
import io.github.joelou.solairelight.brodcast.BroadcastRequestFunctionHandler;
import io.github.joelou.solairelight.brodcast.BroadcastService;
import io.github.joelou.solairelight.config.SolairelightGatewayConfiguration;
import io.github.joelou.solairelight.forward.ForwardService;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.socket.SolairelightWebSocketHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Configuration
@ComponentScan(basePackageClasses= SolairelightPackage.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SolairelightGatewayConfiguration.class}
))
@ConditionalOnProperty(value = "solairelight.enable", havingValue = "true")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(SolairelightProperties.class)
public class SolairelightConfig {

    @Bean
    @DependsOn("solairelightStarter")
    public HandlerMapping handlerMapping(SolairelightProperties properties,
                                         SolairelightWebSocketHandler solairelightWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(properties.getWebsocket().getPath(), solairelightWebSocketHandler);
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
}
