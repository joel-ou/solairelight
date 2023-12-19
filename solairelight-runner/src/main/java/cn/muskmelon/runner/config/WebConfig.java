package cn.muskmelon.runner.config;

import cn.muskmelon.brodercast.BroadcastRequestFunctionHandler;
import cn.muskmelon.brodercast.BroadcastService;
import cn.muskmelon.config.MuskmelonWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
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
@EnableWebFlux
public class WebConfig {
}
