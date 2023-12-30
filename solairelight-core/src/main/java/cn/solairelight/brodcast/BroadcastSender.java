package cn.solairelight.brodcast;

import cn.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
public interface BroadcastSender {

    void send(BasicSession basicSession, Object message);
}
