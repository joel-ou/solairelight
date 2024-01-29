package io.github.joelou.solairelight.cluster;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Joel Ou
 */
@Slf4j
class DistributeWebClient {
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

    public static Mono<ResponseEntity<DistributeResult>> post(String uri,
                                                                   Object body){
        return post(uri, body, null);
    }

    public static Mono<ResponseEntity<DistributeResult>> post(String uri, Object body, MultiValueMap<String, String> headers){
        return webClient
                .post()
                .uri(uri)
                .headers(curHeaders-> {
                    if(headers != null){
                        curHeaders.addAll(headers);
                    }
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(DistributeResult.class)
                .doOnError(e-> log.error("error occurred. url {}", uri, e));
    }
}
