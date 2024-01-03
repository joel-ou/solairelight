package com.github.joelou.solairelight.socket;

import com.github.joelou.solairelight.brodcast.BroadcastSender;
import com.github.joelou.solairelight.exception.UnsupportedBroadcastingMessageException;
import com.github.joelou.solairelight.session.BasicSession;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

/**
 * @author Joel Ou
 */
@Service
public class WebSocketSender implements BroadcastSender {
    @Override
    public void send(BasicSession basicSession, Object message) {
        WebSocketSessionExpand session = ((WebSocketSessionExpand) basicSession);
        if(message instanceof String){
            String messageStr = message.toString();
            if(Base64.isBase64(messageStr)){
                sendBytes(session, Base64.decodeBase64(messageStr));
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
