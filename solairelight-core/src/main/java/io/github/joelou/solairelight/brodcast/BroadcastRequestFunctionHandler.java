package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.cluster.DistributeResult;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.exception.ExceptionEnum;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import io.github.joelou.solairelight.filter.FilterContext;
import io.github.joelou.solairelight.filter.factory.FilterFactory;
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

    public static RouterFunction<ServerResponse> broadcast(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/broadcast", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            BroadcastHttpResponse response = new BroadcastHttpResponse();
            return broadcastParam
                    .handle((param, sink)->{
                        MessageWrapper mw = MessageWrapper.create(param);
                        FilterContext<Object> result = FilterFactory.outgoingMessage().filter(FilterContext.init(mw));
                        if(result.isPass()) {
                            sink.next(mw);
                        } else {
                            sink.error(new ResponseMessageException(ExceptionEnum.FILTER_ABORTED));
                        }
                    })
                    .map(obj->((MessageWrapper)obj).getMessage())
                    //do broadcast.
                    .flatMapMany(message-> broadcastService.broadcast((BroadcastParam) message))
                    .doOnNext(response::add)
                    .last()
                    .map(x->response)
                    .doOnError(e-> log.error("unexpected broadcast failed.", e))
                    .onErrorReturn(BroadcastHttpResponse.failure("unexpected broadcast failed."))
                    .flatMap(x->ServerResponse.ok().bodyValue(x));
        }).build();
    }

    public static RouterFunction<ServerResponse> distributorEntrance(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/distributor/entrance", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            return broadcastParam
                    .doOnNext(broadcastService::distributorEntrance)
                    .map(bol-> DistributeResult.success(NodeData.instance.getBasicInfo()))
                    .flatMap(response->ServerResponse.ok().bodyValue(response))
                    .onErrorResume(ResponseMessageException.class, e->{
                        log.error("broadcast failed {}", e.getMessage());
                        return ServerResponse.ok().bodyValue(DistributeResult.failure(e.getCode(), e.getMessage()));
                    }).onErrorResume(Exception.class, e->{
                        log.error("broadcast failed", e);
                        return ServerResponse.badRequest().bodyValue(DistributeResult.failure("e00", "unknown error"));
                    });
        }).build();
    }
}
