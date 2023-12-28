package cn.solairelight.filter.factory;

import cn.solairelight.filter.SolairelightFilter;
import cn.solairelight.filter.chain.FilterChain;
import cn.solairelight.filter.chain.IncomingMessageFilterChain;
import cn.solairelight.filter.chain.OutgoingMessageFilterChain;
import cn.solairelight.filter.chain.SessionFilterChain;
import cn.solairelight.filter.message.MessageFilter;
import cn.solairelight.filter.session.SessionFilter;
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
                    case outgoing:
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
