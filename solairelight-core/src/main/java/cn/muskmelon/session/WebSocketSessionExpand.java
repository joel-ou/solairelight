package cn.muskmelon.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

/**
 * @author Joel Ou
 */
@Getter
public class WebSocketSessionExpand extends BasicSession {
    private final WebSocketSession originalSession;

    @Setter
    private FluxSink<WebSocketMessage> sink;

    public WebSocketSessionExpand(WebSocketSession webSocketSession){
        this.originalSession = webSocketSession;
        super.setSessionId(webSocketSession.getId());
    }

    public static WebSocketSessionExpand create(WebSocketSession webSocketSession){
        return new WebSocketSessionExpand(webSocketSession);
    }

    @Override
    public void close() {
        super.setClosed(true);
        if(originalSession.isOpen()) {
            originalSession.close(CloseStatus.NORMAL);
            sink.complete();
        }
    }
}
