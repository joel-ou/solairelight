package cn.solairelight.socket;

import cn.solairelight.MessageWrapper;
import cn.solairelight.event.EventContext;
import cn.solairelight.event.EventFactory;
import cn.solairelight.event.EventTrigger;
import cn.solairelight.filter.FilterContext;
import cn.solairelight.filter.factory.FilterFactory;
import cn.solairelight.forward.ForwardService;
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
        if(!result.isPass()) return Mono.empty().then();

        //get receiver
        Flux<MessageWrapper> receiver = handleReceive(sessionExpand, session.receive());
        //create message flux for client
        Flux<WebSocketMessage> sender = Flux.create(sessionExpand::setSink);
        handleSender(sessionExpand, sender);

        //trigger events
        EventFactory
                .getTrigger(EventContext.EventType.SESSION_CONNECTED)
                .call(sessionExpand)
                .subscribe();
        log.debug("session accepted. sessionId: {}, handshakeInfo: {}", sessionExpand.getSessionId(), session.getHandshakeInfo());
        return Flux.zip(receiver, session.send(sender))
                .doOnError(error-> log.error("session error occurred ", error))
                .doFinally(signalType -> {
                    log.debug("session finished. sessionId: {}, reason : {}", sessionExpand.getSessionId(),
                            signalType);
                    EventFactory
                            .getTrigger(EventContext.EventType.SESSION_DISCONNECTED)
                            .call(sessionExpand).subscribe();
                })
                .then();
    }

    private Flux<MessageWrapper> handleReceive(WebSocketSessionExpand sessionExpand,
                                                 Flux<WebSocketMessage> receiver){
        EventTrigger eventTrigger = EventFactory.getTrigger(EventContext.EventType.MESSAGE);
        return receiver
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
                .map(obj->(MessageWrapper)obj)
                .doOnNext(message->{
                    //do forward
                    forwardService.forward(sessionExpand, message);
                    //trigger events after filter execute.
                    eventTrigger
                            .call(message)
                            .doFinally(s->((WebSocketMessage) message.getRawMessage()).release()).subscribe();
                })
                .onErrorComplete()
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
