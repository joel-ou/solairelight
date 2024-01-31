package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.cluster.NodeBroadcastingResponse;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.exception.ExceptionEnum;
import io.github.joelou.solairelight.exception.ResponseMessageException;
import io.github.joelou.solairelight.filter.FilterContext;
import io.github.joelou.solairelight.filter.factory.FilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * @author Joel Ou
 */
@Slf4j
public class BroadcastRequestFunctionHandler {
    private final static Pattern pattern = Pattern.compile("^\\w+={2}((\\d+|-?\\d+.\\d+)+|'\\w+')(\\s*(&{2}|[|]{2}|and|or)\\s*\\w+={2}((\\d+|-*\\d+.{1}\\d+)+|'\\w+'))*$");

    public static RouterFunction<ServerResponse> broadcast(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/broadcast", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            return broadcastParam
                    .handle((param, sink)->{
                        MessageWrapper mw = MessageWrapper.create(param);
                        if (StringUtils.hasText(param.getPredicate()) && !checkElString(param.getPredicate())) {
                            sink.error(new ResponseMessageException(ExceptionEnum.INVALID_PREDICATE_VALUE));
                            return;
                        }
                        FilterContext<Object> result = FilterFactory.outgoingMessage().filter(FilterContext.init(mw));
                        if(result.isPass()) {
                            sink.next(mw);
                        } else {
                            sink.error(new ResponseMessageException(ExceptionEnum.FILTER_ABORTED));
                        }
                    })
                    .map(obj->((MessageWrapper)obj).getMessage())
                    //do broadcast.
                    .flatMap(message-> broadcastService.broadcast((BroadcastParam) message))
                    .doOnError(e-> log.error("unexpected broadcast failed.", e))
                    .onErrorResume(e->{
                        if(e instanceof ResponseMessageException) {
                            return Mono.just(NodeBroadcastingResponse.failure(((ResponseMessageException) e).getCode(), e.getMessage()));
                        }
                        return Mono.just(NodeBroadcastingResponse.failure(ExceptionEnum.UNEXPECTED_BROADCAST_ERROR.getCode(),
                                ExceptionEnum.UNEXPECTED_BROADCAST_ERROR.getMessage()));
                    })
                    .flatMap(x->ServerResponse.ok().bodyValue(x));
        }).build();
    }

    @Deprecated
    public static RouterFunction<ServerResponse> distributorEntrance(BroadcastService broadcastService){
        return RouterFunctions.route().POST("solairelight/distributor/entrance", request -> {
            Mono<BroadcastParam> broadcastParam = request.bodyToMono(BroadcastParam.class);
            return broadcastParam
                    .doOnNext(broadcastService::distributorEntrance)
                    .map(bol-> NodeBroadcastingResponse.success(NodeData.instance.getBasicInfo()))
                    .flatMap(response->ServerResponse.ok().bodyValue(response))
                    .onErrorResume(ResponseMessageException.class, e->{
                        log.error("broadcast failed {}", e.getMessage());
                        return ServerResponse.ok().bodyValue(NodeBroadcastingResponse.failure(e.getCode(), e.getMessage()));
                    }).onErrorResume(Exception.class, e->{
                        log.error("broadcast failed", e);
                        return ServerResponse.badRequest().bodyValue(NodeBroadcastingResponse.failure("e00", "unknown error"));
                    });
        }).build();
    }

    private static boolean checkElString(String el){
        return pattern.matcher(el).matches();
    }
}
