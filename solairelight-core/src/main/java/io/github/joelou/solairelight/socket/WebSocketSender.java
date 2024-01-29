package io.github.joelou.solairelight.socket;

import io.github.joelou.solairelight.brodcast.BroadcastSender;
import io.github.joelou.solairelight.exception.UnsupportedBroadcastingMessageException;
import io.github.joelou.solairelight.session.BasicSession;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

/**
 * @author Joel Ou
 */
@Service
public class WebSocketSender implements BroadcastSender {
    private final String BASE64_PREFIX = "base64:";

    @Override
    public void send(BasicSession basicSession, Object message) {
        WebSocketSessionExpand session = ((WebSocketSessionExpand) basicSession);
        if(message instanceof String){
            String messageStr = message.toString();
            if(messageStr.startsWith(BASE64_PREFIX)){
                sendBytes(session, Base64Utils.decode(messageStr.replace(BASE64_PREFIX, "").getBytes()));
            } else {
                session.getSink()
                        .next(session.getOriginalSession().textMessage(messageStr));
            }
            return;
        } else if (message instanceof byte[]){
            sendBytes(session, (byte[]) message);
            return;
        }
        throw new UnsupportedBroadcastingMessageException();
    }

    private void sendBytes(WebSocketSessionExpand session, byte[] bytes){
        session.getSink()
                .next(session.getOriginalSession().binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(bytes)));
    }
}
