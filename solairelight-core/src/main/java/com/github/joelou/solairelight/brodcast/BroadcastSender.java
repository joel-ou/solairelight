package com.github.joelou.solairelight.brodcast;

import com.github.joelou.solairelight.session.BasicSession;

/**
 * @author Joel Ou
 */
public interface BroadcastSender {

    void send(BasicSession basicSession, Object message);
}
