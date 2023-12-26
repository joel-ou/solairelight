package cn.solairelight.filter.factory;

import cn.solairelight.filter.chain.FilterChain;
import cn.solairelight.filter.chain.IncomingMessageFilterChain;
import cn.solairelight.filter.chain.OutgoingMessageFilterChain;
import cn.solairelight.filter.chain.SessionFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Joel Ou
 */
@Component
public class FilterFactory {

    private SessionFilterChain sessionFilterChain;

    private IncomingMessageFilterChain incomingMessageFilterChain;

    private OutgoingMessageFilterChain outgoingMessageFilterChain;

    private static FilterFactory instance;

    @Autowired
    public FilterFactory(SessionFilterChain sessionFilterChain, IncomingMessageFilterChain incomingMessageFilterChain, OutgoingMessageFilterChain outgoingMessageFilterChain) {
        this.sessionFilterChain = sessionFilterChain;
        this.incomingMessageFilterChain = incomingMessageFilterChain;
        this.outgoingMessageFilterChain = outgoingMessageFilterChain;
    }

    private FilterFactory() {}

    @PostConstruct
    private void postConstruct() {
        instance = this;
    }

    public static FilterChain session(){
        return instance.sessionFilterChain;
    }

    public static FilterChain incomingMessage(){
        return instance.incomingMessageFilterChain;
    }

    public static FilterChain outgoingMessage(){
        return instance.outgoingMessageFilterChain;
    }
}
