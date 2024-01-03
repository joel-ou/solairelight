package com.github.joelou.solairelight.filter.chain;

import com.github.joelou.solairelight.filter.message.MessageFilter;

import java.util.Set;

/**
 * @author Joel Ou
 */
public class IncomingMessageFilterChain extends AbstractFilterChain {

    public IncomingMessageFilterChain(Set<MessageFilter> filters) {
        super(filters);
    }
}
