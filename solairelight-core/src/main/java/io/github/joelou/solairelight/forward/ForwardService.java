package io.github.joelou.solairelight.forward;

import io.github.joelou.solairelight.MessageWrapper;
import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.expression.ExpressionEvaluator;
import io.github.joelou.solairelight.expression.Operator;
import io.github.joelou.solairelight.expression.SpringExpressionEvaluator;
import io.github.joelou.solairelight.properties.RouteProperties;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.github.joelou.solairelight.session.BasicSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParser;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Service
@Slf4j
public class ForwardService {
    @Resource
    private SolairelightProperties solairelightProperties;

    @Getter
    private static final String xForwardForKey = "X-Forwarded-For";

    static {
        ClusterTools.getLocalIPAddress();
    }

    public Mono<?> forward(BasicSession basicSession, MessageWrapper message){
        if(!message.isForwardable() || message.getFeatures() == null) {
            log.debug("message is not forwardable. {}", message);
            return Mono.empty();
        }
        try {
            if (!solairelightProperties.getForward().isEnable()) {
                log.debug("forward not enabled.");
                return Mono.empty();
            }
            Object messageObj = message.getMessage();
            String uri = routing(basicSession, message.getFeatures());
            if (uri == null) {
                log.warn("no route matched. message: {}  config: {}", message.getMessage(), solairelightProperties.getForward());
                return Mono.empty();
            }
            return ForwardWebClient
                    .post(URI.create(uri), messageObj, headerHandle(basicSession))
                    .doOnNext(response -> {
                        log.debug("forwarded response, status {} body {}", response.getStatusCode(), response.getBody());
                    });
        } catch (JsonParseException jsonError) {
            log.error("json parse error.", jsonError);
        } catch (Exception e) {
            //catch all exception for avoid the flux stream abort
            log.error("forward error occurred. message: {}  config: {}", message.getMessage(), solairelightProperties.getForward(), e);
        }
        return Mono.empty();
    }

    @Nullable
    public String routing(BasicSession basicSession, Object messageFeature){
        ExpressionEvaluator<Object> evaluator = new SpringExpressionEvaluator<>();
        for (RouteProperties routeProperties : solairelightProperties.getForward().getRoutes()) {
            RouteProperties.Predicate predicate = routeProperties.getPredicate();
            //evl message predicate
            boolean messageResult=false, sessionResult=false;
            if(StringUtils.hasText(predicate.getMessage())){
                messageResult = evaluator.evaluate(predicate.getMessage(), messageFeature);
            }
            //evl session header predicate
            if(StringUtils.hasText(predicate.getSessionHeader())){
                sessionResult = evaluator.evaluate(predicate.getSessionHeader(), basicSession.getSessionHeads());
            }
            boolean result = predicate.getOperator()== Operator.AND?messageResult&&sessionResult:messageResult||sessionResult;
            if(result){
                return routeProperties.getUri();
            }
        }
        return null;
    }

    @Nullable
    private MultiValueMap<String, String> headerHandle(BasicSession basicSession){
        String forwardHeader = solairelightProperties.getForward().getForwardHeader();
        if(!StringUtils.hasText(forwardHeader)){
            return null;
        }
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<>();
        for (String headerKey : forwardHeader.split(",")) {
            if(headerKey.contains("=")){
                String[] kv = headerKey.split("=");
                newHeaders.put(kv[0], Collections.singletonList(kv[0]));
            } else {
                String v = basicSession.getSessionHeads().get(headerKey);
                newHeaders.put(headerKey, Collections.singletonList(v));
            }
        }

        String localIPAddress = ClusterTools.getLocalIPAddress();
        if(localIPAddress == null){
            throw new RuntimeException("local ip is empty.");
        }
        //handle X-Forwarded-For
        String forwardFor = basicSession.getSessionHeads().get(xForwardForKey);
        if(StringUtils.hasText(forwardFor)){
            newHeaders.put(xForwardForKey, Collections.singletonList(forwardFor+","+localIPAddress));
        } else {
            newHeaders.put(xForwardForKey, Collections.singletonList(basicSession.getClientIP()+","+localIPAddress));
        }
        return newHeaders;
    }

    private Map<String, Object> messageToJson(WebSocketMessage webSocketMessage){
        String json;
        switch (webSocketMessage.getType()) {
            case TEXT:
                json = webSocketMessage.getPayloadAsText();
                break;
            case BINARY:
                json = webSocketMessage.getPayload().toString();
                break;
            default:
                return null;
        }
        JsonParser jsonParser = new BasicJsonParser();
        return jsonParser.parseMap(json);
    }
}
