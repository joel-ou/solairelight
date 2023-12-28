package cn.solairelight.config;

import cn.solairelight.MessageWrapper;
import cn.solairelight.event.EventContext;
import cn.solairelight.event.EventFactory;
import cn.solairelight.event.EventTrigger;
import cn.solairelight.filter.FilterContext;
import cn.solairelight.filter.factory.FilterFactory;
import cn.solairelight.forward.ForwardService;
import cn.solairelight.session.WebSocketSessionExpand;
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
        FilterFactory.session().execute(FilterContext.init(sessionExpand));

        //get receiver
        Flux<WebSocketMessage> receiver = session.receive();
        receiver = handleReceive(sessionExpand, receiver);
        //create message flux for client
        Flux<WebSocketMessage> sender = Flux.create(sessionExpand::setSink);
        handleSender(sessionExpand, sender);

        //trigger events
        EventFactory
                .getTrigger(EventContext.EventType.SESSION_CONNECTED)
                .call(sessionExpand).subscribe();
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

    private Flux<WebSocketMessage> handleReceive(WebSocketSessionExpand sessionExpand,
                                                 Flux<WebSocketMessage> receiver){
        EventTrigger eventTrigger = EventFactory.getTrigger(EventContext.EventType.MESSAGE);
        return receiver
                .doOnNext(message->{
                    message.retain();
                    MessageWrapper<WebSocketMessage> messageWrapper = MessageWrapper.create(message);
                    log.debug("receive message from client, message: {}", message.getPayloadAsText());
                    //executing filters
                    FilterContext<?> result = FilterFactory.incomingMessage().execute(FilterContext.init(messageWrapper));
                    if(!result.isPass()){
                        log.info("message aborted at {}. no exception threw.", result.getAbortPoint().getName());
                        return;
                    }
                    //do forward
                    forwardService.forward(sessionExpand, (MessageWrapper<Object>) result.getPayload());

                    //trigger events after filter execute.
                    eventTrigger
                            .call(result.getPayload())
                            .doFinally(s->message.release()).subscribe();
                })
                .onErrorComplete()
                .doOnError(error-> log.error("receiving message error occurred ", error));
    }

    private void handleSender(WebSocketSessionExpand sessionExpand, Flux<WebSocketMessage> sender){
        sender.doOnNext(message->{
            log.debug("send a message : {}", message.getPayloadAsText());
            //executing filters
            FilterFactory.outgoingMessage().execute(FilterContext.init(message));
        });
    }
}
