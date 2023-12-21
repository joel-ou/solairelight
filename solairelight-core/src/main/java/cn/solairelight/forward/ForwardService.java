package cn.solairelight.forward;

import cn.solairelight.cluster.ClusterTools;
import cn.solairelight.expression.ExpressionEvaluator;
import cn.solairelight.expression.Operator;
import cn.solairelight.expression.SpringExpressionEvaluator;
import cn.solairelight.properties.Route;
import cn.solairelight.properties.SolairelightProperties;
import cn.solairelight.session.BasicSession;
import cn.solairelight.session.WebSocketSessionExpand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParser;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;

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

    public void forward(WebSocketSessionExpand sessionExpand, Object message){
        Map<String, Object> jsonObject = null;
        try {
            if (!solairelightProperties.getForward().isEnable()) {
                log.debug("forward not enabled.");
                return;
            }
            WebSocketMessage webSocketMessage = ((WebSocketMessage) message);
            jsonObject = messageToJson(webSocketMessage);
            String uri = routing(sessionExpand, webSocketMessage, jsonObject);
            if (uri == null) {
                log.warn("no route matched. message: {}  config: {}", jsonObject, solairelightProperties.getForward());
                return;
            }
            ForwardWebClient
                    .post(URI.create(uri), headerHandle(sessionExpand))
                    .doOnNext(response -> {
                        log.debug("forwarded response, status {} body {}", response.getStatusCode(), response.getBody());
                    }).subscribe();
        } catch (JsonParseException jsonError) {
            log.error("json parse error.", jsonError);
        } catch (Exception e) {
            //catch all exception for avoid the flux stream abort
            log.error("forward error occurred. message: {}  config: {}", jsonObject, solairelightProperties.getForward(), e);
        }
    }

    @Nullable
    public String routing(WebSocketSessionExpand sessionExpand, WebSocketMessage webSocketMessage,
                          Map<String, Object> jsonObject){
        ExpressionEvaluator<Object> evaluator = new SpringExpressionEvaluator<>();
        for (Route route : solairelightProperties.getForward().getRoutes()) {
            Route.Predicate predicate = route.getPredicate();
            //evl message predicate
            boolean messageResult=false, sessionResult=false;
            if(StringUtils.hasText(predicate.getMessage())){
                messageResult = evaluator.evaluate(predicate.getMessage(), jsonObject);
            }
            //evl session header predicate
            if(StringUtils.hasText(predicate.getHeader())){
                sessionResult = evaluator.evaluate(predicate.getHeader(), sessionExpand.getSessionHeads());
            }
            boolean result = predicate.getOperator()== Operator.AND?messageResult&&sessionResult:messageResult||sessionResult;
            if(result){
                return route.getUri();
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
        JsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(json);
    }
}
