package cn.solairelight.runner.demo.filter;

import cn.solairelight.MessageWrapper;
import cn.solairelight.filter.FilterContext;
import cn.solairelight.filter.message.MessageFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class MessageOutgoingFilterImpl extends MessageFilter {

    public MessageOutgoingFilterImpl() {
        super(MessageWay.outgoing);
    }

    @Override
    public FilterContext<MessageWrapper> doFilter(FilterContext<MessageWrapper> filterContext) {
        System.out.println(filterContext.getPayload().isForwardable());
        log.info("message outgoing filter. {}", filterContext);
        return filterContext;
    }
}