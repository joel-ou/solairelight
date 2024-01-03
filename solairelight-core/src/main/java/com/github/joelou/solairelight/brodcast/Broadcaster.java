package com.github.joelou.solairelight.brodcast;

import lombok.Getter;

import java.util.LinkedList;

/**
 * @author Joel Ou
 */
public interface Broadcaster {

    enum BroadcastChannel {
        WEBSOCKET("webSocketBroadcaster");

        @Getter
        private String serviceName;

        BroadcastChannel(String serviceName) {
            this.serviceName = serviceName;
        }
    }

    boolean broadcast(BroadcastParam broadcastParam);

    void localBroadcast(BroadcastParam broadcastParam);

    void localBroadcast(LinkedList<String[]> exprList, BroadcastParam broadcastParam);
}
