package com.github.joelou.solairelight.filter.factory;

import com.github.joelou.solairelight.filter.SolairelightFilter;
import com.github.joelou.solairelight.filter.chain.FilterChain;
import com.github.joelou.solairelight.filter.chain.IncomingMessageFilterChain;
import com.github.joelou.solairelight.filter.chain.OutgoingMessageFilterChain;
import com.github.joelou.solairelight.filter.chain.SessionFilterChain;
import com.github.joelou.solairelight.filter.message.MessageFilter;
import com.github.joelou.solairelight.filter.session.SessionFilter;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Joel Ou
 */
public class FilterFactory {

    private SessionFilterChain sessionFilterChain;

    private IncomingMessageFilterChain incomingMessageFilterChain;

    private OutgoingMessageFilterChain outgoingMessageFilterChain;

    @Getter
    private static FilterFactory instance;

    private FilterFactory() {}

    public static void init(Set<SolairelightFilter<?>> filters){
        instance = new FilterFactory();
        Set<SessionFilter> sessionFilter = new LinkedHashSet<>();
        Set<MessageFilter> incomingMessageFilter = new LinkedHashSet<>();
        Set<MessageFilter> outgoingMessageFilter = new LinkedHashSet<>();
        for (SolairelightFilter<?> filter : filters) {
            if(filter instanceof SessionFilter) {
                sessionFilter.add((SessionFilter) filter);
            } else if(filter instanceof MessageFilter){
                MessageFilter messageFilter = ((MessageFilter) filter);
                switch (((MessageFilter) filter).getMessageWay()) {
                    case incoming:
                        incomingMessageFilter.add(messageFilter);
                        break;
                    case broadcast:
                        outgoingMessageFilter.add(messageFilter);
                        break;
                    default:
                        incomingMessageFilter.add(messageFilter);
                        outgoingMessageFilter.add(messageFilter);
                        break;
                }
            }
        }
        instance.sessionFilterChain = new SessionFilterChain(sessionFilter);
        instance.incomingMessageFilterChain = new IncomingMessageFilterChain(incomingMessageFilter);
        instance.outgoingMessageFilterChain = new OutgoingMessageFilterChain(outgoingMessageFilter);
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
