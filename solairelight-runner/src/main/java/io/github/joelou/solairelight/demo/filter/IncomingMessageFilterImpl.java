package io.github.joelou.solairelight.demo.filter;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.filter.FilterContext;
import io.github.joelou.solairelight.filter.message.MessageFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class IncomingMessageFilterImpl extends MessageFilter {

    public IncomingMessageFilterImpl() {
        super(MessageWay.incoming);
    }

    @Override
    public FilterContext<MessageWrapper> doFilter(FilterContext<MessageWrapper> filterContext) {
        log.info("message incoming filter. {}", filterContext);
        return FilterContext.pass(filterContext);
    }
}
