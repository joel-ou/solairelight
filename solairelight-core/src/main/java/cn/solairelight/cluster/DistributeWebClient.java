package cn.solairelight.cluster;

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
public class DistributeWebClient {
    private static final WebClient webClient;

    static {
        HttpClient httpClient = HttpClient.create()
                .keepAlive(true)
                .responseTimeout(Duration.ofSeconds(2))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.MAX_MESSAGES_PER_WRITE, 1000);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        webClient = WebClient.builder().clientConnector(connector).build();
    }

    public static Mono<ResponseEntity<String>> post(URI uri,
                                                    Object body){
        return post(uri, body, null);
    }

    public static Mono<ResponseEntity<String>> post(URI uri,
                                                    Object body,
                                                    MultiValueMap<String, String> headers){
        return webClient
                .post()
                .uri(uri)
                .headers(curHeaders-> {
                    if(headers != null){
                        curHeaders.addAll(headers);
                    }
                })
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }
}
