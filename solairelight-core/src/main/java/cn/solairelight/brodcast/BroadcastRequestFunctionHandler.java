package cn.solairelight.brodcast;

import cn.solairelight.exception.ResponseMessageException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author Joel Ou
 */
@Slf4j
public class BroadcastRequestFunctionHandler {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class HttpResponse {

        private boolean success = true;

        private String code = "success";

        private String message = "success";

        public static HttpResponse success(){
            return new HttpResponse();
        }

        public static HttpResponse failure(String code, String message){
            return new HttpResponse(false, code, message);
        }
    }

    public static RouterFunction<ServerResponse> broadcast(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/broadcast", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            return broadcastParam
                    .doOnNext(broadcastService::broadcast)
                    .map(bol->HttpResponse.success())
                    .flatMap(response->ServerResponse.ok().bodyValue(response))
                    .onErrorResume(ResponseMessageException.class, e->{
                        log.info("broadcast failed {}", e.getMessage());
                        return ServerResponse.badRequest().bodyValue(HttpResponse.failure(e.getCode(), e.getMessage()));
                    }).onErrorResume(Exception.class, e->{
                        log.error("broadcast failed", e);
                        return ServerResponse.badRequest().bodyValue(HttpResponse.failure("e00", "unknown error"));
                    });
        }).build();
    }

    public static RouterFunction<ServerResponse> distributorEntrance(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/distributor/entrance", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            return broadcastParam
                    .doOnNext(broadcastService::distributorEntrance)
                    .map(bol->HttpResponse.success())
                    .flatMap(response->ServerResponse.ok().bodyValue(response))
                    .onErrorResume(ResponseMessageException.class, e->{
                        log.info("broadcast failed {}", e.getMessage());
                        return ServerResponse.badRequest().bodyValue(HttpResponse.failure(e.getCode(), e.getMessage()));
                    }).onErrorResume(Exception.class, e->{
                        log.error("broadcast failed", e);
                        return ServerResponse.badRequest().bodyValue(HttpResponse.failure("e00", "unknown error"));
                    });
        }).build();
    }
}