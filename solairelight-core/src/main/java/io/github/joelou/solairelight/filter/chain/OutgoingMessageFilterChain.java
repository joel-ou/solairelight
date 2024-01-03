package io.github.joelou.solairelight.filter.chain;

import io.github.joelou.solairelight.filter.message.MessageFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class OutgoingMessageFilterChain extends AbstractFilterChain {

    public OutgoingMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
