package com.github.joelou.solairelight.demo.filter;

import com.github.joelou.solairelight.MessageWrapper;
import com.github.joelou.solairelight.filter.FilterContext;
import com.github.joelou.solairelight.filter.message.MessageFilter;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * this class show how to parse you owen message.<br/>
 * and extract features of message into the message wrapper.<br/>
 * that is how forward working by message features.<br/>
 * this is an example do not use it directly.
 * @author Joel Ou
 */
@Component
public class MessageFeatureFilter extends MessageFilter {

    public MessageFeatureFilter() {
        super(MessageWay.incoming);
    }

    @Override
    public FilterContext<MessageWrapper> doFilter(FilterContext<MessageWrapper> filterContext) {
        MessageWrapper messageWrapper = filterContext.getPayload();
        WebSocketMessage webSocketMessage = ((WebSocketMessage) filterContext.getPayload().getRawMessage());
        Map<String, Object> features = new HashMap<>();

        //parse message. this just an example, do not use it directly.
        JsonParser jsonParser = new BasicJsonParser();
        String json = webSocketMessage.getPayload().toString(StandardCharsets.UTF_8);
        Map<String, Object> messageMap = jsonParser.parseMap(json);
        for (Map.Entry<String, Object> entry : messageMap.entrySet()) {
            if(entry.getKey().equals("sampleKey"))
                features.put(entry.getKey(), entry.getValue());
        }
        messageWrapper.setMessage(messageMap);
        messageWrapper.setFeatures(features);
        messageWrapper.setForwardable(true);
        return filterContext;
    }

    @Override
    public int order() {
        return 0;
    }
}
