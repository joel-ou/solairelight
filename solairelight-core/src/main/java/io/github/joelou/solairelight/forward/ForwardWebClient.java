package io.github.joelou.solairelight.forward;

import io.netty.channel.ChannelOption;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;

/**
 * @author Joel Ou
 */
public class ForwardWebClient {
    private static WebClient webClient;

    private static WebClient.Builder loadbalancedWebClientBuilder;

    public synchronized static void init(WebClient.Builder builder){
        if(loadbalancedWebClientBuilder!=null)return;
        ForwardWebClient.loadbalancedWebClientBuilder = builder;

        HttpClient httpClient = HttpClient.create()
                .keepAlive(true)
                .responseTimeout(Duration.ofSeconds(2))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.MAX_MESSAGES_PER_WRITE, 10000);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        webClient = loadbalancedWebClientBuilder.clientConnector(connector).build();
    }

    public static Mono<ResponseEntity<String>> post(URI uri, Object body, MultiValueMap<String, String> headers){
        return webClient
                .post()
                .uri(uri)
                .bodyValue(body)
                .headers(curHeaders-> curHeaders.addAll(headers))
                .retrieve()
                .toEntity(String.class);
    }
}
