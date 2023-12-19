package cn.muskmelon.brodercast;

import lombok.Getter;

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
}
