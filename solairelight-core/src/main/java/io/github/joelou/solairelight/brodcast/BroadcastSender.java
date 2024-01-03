package io.github.joelou.solairelight.brodcast;

import io.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
public interface BroadcastSender {

    void send(BasicSession basicSession, Object message);
}
