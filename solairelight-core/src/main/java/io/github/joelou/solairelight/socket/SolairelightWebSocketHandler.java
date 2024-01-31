package io.github.joelou.solairelight.socket;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.cluster.NodeData;
import io.github.joelou.solairelight.event.EventFactory;
import io.github.joelou.solairelight.event.EventTrigger;
import io.github.joelou.solairelight.filter.FilterContext;
import io.github.joelou.solairelight.filter.factory.FilterFactory;
import io.github.joelou.solairelight.forward.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Joel Ou
 */
@Slf4j
public class SolairelightWebSocketHandler implements WebSocketHandler {

    private final ForwardService forwardService;

    public SolairelightWebSocketHandler(ForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.debug("new session from {}", session.getHandshakeInfo());
        WebSocketSessionExpand sessionExpand = WebSocketSessionExpand.create(session);

        //executing the filter chain of session
        FilterContext<Object> result = FilterFactory.session().filter(FilterContext.init(sessionExpand));
        if(!result.isPass()) {
            //recover session number.
            NodeData.instance.getSessionQuota().incrementAndGet();
            return Mono.empty().then();
        }

        //get receiver
        Flux<?> receiver = handleReceive(sessionExpand, session.receive());
        //create message flux for client
        Flux<WebSocketMessage> sender = Flux.create(sessionExpand::setSink);
        handleSender(sessionExpand, sender);

        //trigger events
        EventFactory
                .getTrigger(EventTrigger.TriggerAction.SESSION_CONNECTED)
                .call(sessionExpand)
                .subscribe();
        log.debug("session accepted. sessionId: {}, handshakeInfo: {}", sessionExpand.getSessionId(), session.getHandshakeInfo());
        return Flux.zip(receiver, session.send(sender))
                .doOnError(error-> log.error("session error occurred ", error))
                .doFinally(signalType -> {
                    log.debug("session finished. sessionId: {}, reason : {}", sessionExpand.getSessionId(),
                            signalType);
                    EventFactory
                            .getTrigger(EventTrigger.TriggerAction.SESSION_DISCONNECTED)
                            .call(sessionExpand).subscribe();
                })
                .then();
    }

    private Flux<?> handleReceive(WebSocketSessionExpand sessionExpand,
                                                 Flux<WebSocketMessage> receiver){
        EventTrigger eventTrigger = EventFactory.getTrigger(EventTrigger.TriggerAction.MESSAGE);
        return receiver
                .limitRate(1)
                .handle((message, sink)->{
                    message.retain();
                    log.debug("receive message from client, message: {}", message.getPayloadAsText());
                    MessageWrapper messageWrapper = MessageWrapper.create(message);
                    //executing filters
                    FilterContext<?> result = FilterFactory.incomingMessage().filter(FilterContext.init(messageWrapper));
                    if(result.isPass()){
                        sink.next(result.getPayload());
                    } else {
                        message.release();
                    }
                })
                .checkpoint("filter phase")
                .map(obj->(MessageWrapper)obj)
                .doOnNext(message->{
                    //do forward
                    forwardService.forward(sessionExpand, message);
                    //trigger events after filter execute.
                    eventTrigger
                            .call(message)
                            .doFinally(s->((WebSocketMessage) message.getRawMessage()).release()).subscribe();
                })
                .concatMap(messageWrapper -> forwardService.forward(sessionExpand, messageWrapper))
                .checkpoint("forward phase")
                .doOnError(error-> log.error("receiving message error occurred ", error));
    }

    private void handleSender(WebSocketSessionExpand sessionExpand, Flux<WebSocketMessage> sender){
        sender.doOnNext(message->{
            log.debug("send a message : {}", message.getPayloadAsText());
            //executing filters
            FilterFactory.outgoingMessage().filter(FilterContext.init(message));
        });
    }
}
