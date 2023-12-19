package cn.muskmelon.config;

import cn.muskmelon.event.EventContext;
import cn.muskmelon.event.EventFactory;
import cn.muskmelon.filter.FilterCargo;
import cn.muskmelon.filter.factory.FilterFactory;
import cn.muskmelon.forward.ForwardService;
import cn.muskmelon.session.WebSocketSessionExpand;
import jakarta.annotation.Resource;
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
public class MuskmelonWebSocketHandler implements WebSocketHandler {

    @Resource
    private ForwardService forwardService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.debug("new session from {}", session.getHandshakeInfo());
        WebSocketSessionExpand sessionExpand = WebSocketSessionExpand.create(session);
        //executing the filter chain of session
        FilterFactory.session().execute(FilterCargo.init(sessionExpand));

        //get receiver
        Flux<WebSocketMessage> receiver = session.receive();
        receiver = handleReceive(sessionExpand, receiver);
        //create message flux for client
        Flux<WebSocketMessage> sender = Flux.create(sessionExpand::setSink);
        handleSender(sessionExpand, sender);

        //trigger events
        EventFactory
                .getTrigger(EventContext.EventType.SESSION_CONNECTED)
                .call(sessionExpand);
        log.debug("session accepted. sessionId: {}, handshakeInfo: {}", sessionExpand.getSessionId(), session.getHandshakeInfo());
        return Flux.zip(receiver, session.send(sender))
                .doOnError(error-> log.error("session error occurred ", error))
                .doFinally(signalType -> {
                    log.debug("session finished. sessionId: {}, reason : {}", sessionExpand.getSessionId(),
                            signalType);
                    EventFactory
                            .getTrigger(EventContext.EventType.SESSION_DISCONNECTED)
                            .call(sessionExpand);
                })
                .then();
    }

    private Flux<WebSocketMessage> handleReceive(WebSocketSessionExpand sessionExpand,
                                                 Flux<WebSocketMessage> receiver){
        return receiver.doOnNext(message->{
            log.debug("receive message from client, message: {}", message.getPayloadAsText());
            //executing filters
            FilterCargo<?> result = FilterFactory.inboundMessage().execute(FilterCargo.init(message));
            //do forward
            forwardService.forward(sessionExpand, result.getPayload());
            //trigger events
            EventFactory.getTrigger(EventContext.EventType.OUTGOING_MESSAGE).call(result.getPayload());
        }).doOnError(error-> log.error("receiving message error occurred ", error));
    }

    private void handleSender(WebSocketSessionExpand sessionExpand, Flux<WebSocketMessage> sender){
        sender.doOnNext(message->{
            log.debug("send a message : {}", message.getPayloadAsText());
            //executing filters
            FilterFactory.outboundMessage().execute(FilterCargo.init(message));
            //trigger events
            EventFactory.getTrigger(EventContext.EventType.OUTGOING_MESSAGE).call(message);
        });
    }
}
