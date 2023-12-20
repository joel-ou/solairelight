package cn.solairelight.filter.factory;

import cn.solairelight.filter.chain.FilterChain;
import cn.solairelight.filter.chain.InboundMessageFilterChain;
import cn.solairelight.filter.chain.OutboundMessageFilterChain;
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

    private InboundMessageFilterChain inboundMessageFilterChain;

    private OutboundMessageFilterChain outboundMessageFilterChain;

    private static FilterFactory instance;

    @Autowired
    public FilterFactory(SessionFilterChain sessionFilterChain, InboundMessageFilterChain inboundMessageFilterChain, OutboundMessageFilterChain outboundMessageFilterChain) {
        this.sessionFilterChain = sessionFilterChain;
        this.inboundMessageFilterChain = inboundMessageFilterChain;
        this.outboundMessageFilterChain = outboundMessageFilterChain;
    }

    private FilterFactory() {}

    @PostConstruct
    private void postConstruct() {
        instance = this;
    }

    public static FilterChain session(){
        return instance.sessionFilterChain;
    }

    public static FilterChain inboundMessage(){
        return instance.inboundMessageFilterChain;
    }

    public static FilterChain outboundMessage(){
        return instance.outboundMessageFilterChain;
    }
}
