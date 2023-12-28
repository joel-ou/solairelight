package cn.solairelight.runner.demo.filter;

import cn.solairelight.MessageWrapper;
import cn.solairelight.filter.FilterContext;
import cn.solairelight.filter.message.MessageFilter;
import org.apache.commons.codec.Charsets;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
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
        String json = webSocketMessage.getPayload().toString(StandardCharsets.UTF_8);
        Map<String, Object> features = new HashMap<>();
        JsonParser jsonParser = new BasicJsonParser();
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
