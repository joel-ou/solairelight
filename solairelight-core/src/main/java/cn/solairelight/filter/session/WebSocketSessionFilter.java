package cn.solairelight.filter.session;

import cn.solairelight.filter.FilterContext;
import cn.solairelight.session.BasicSession;
import cn.solairelight.session.SessionBroker;
import cn.solairelight.session.WebSocketSessionExpand;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.HandshakeInfo;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Component
public class WebSocketSessionFilter implements SessionFilter {

    @Override
    public FilterContext<BasicSession> execute(FilterContext<BasicSession> filterContext) {
        Object payload = filterContext.getPayload();
        WebSocketSessionExpand socketSessionExpand = (WebSocketSessionExpand) payload;

        Map<String, String> sessionHeads = socketSessionExpand.getSessionHeads();
        //handle session heads;
        HandshakeInfo handshakeInfo = socketSessionExpand.getOriginalSession().getHandshakeInfo();
        handshakeInfo.getHeaders().forEach((k,vs)-> sessionHeads.put(k, vs.get(0)));
        //set client IP
        String realIP = getClientRealIP(socketSessionExpand);
        if(realIP != null){
            socketSessionExpand.setClientIP(realIP);
        } else {
            InetSocketAddress inetSocketAddress = socketSessionExpand.getOriginalSession().getHandshakeInfo().getRemoteAddress();
            if(inetSocketAddress != null)socketSessionExpand.setClientIP(inetSocketAddress.getHostString());
        }

        //parse url params
        String urlQuery = handshakeInfo.getUri().getQuery();
        if(urlQuery!=null) {
            for (String p : urlQuery.split("&")) {
                String[] kv = p.split("=");
                if(kv.length < 2){
                    continue;
                }
                sessionHeads.put(kv[0], kv[1]);
            }
        }

        //storage the session
        SessionBroker.getStorage().put(socketSessionExpand.getSessionId(), socketSessionExpand);
        return FilterContext.pass(socketSessionExpand);
    }

    @Override
    public int order() {
        return -99;
    }

    @Nullable
    private String getClientRealIP(WebSocketSessionExpand session){
        HandshakeInfo handshakeInfo = session.getOriginalSession().getHandshakeInfo();
        HttpHeaders headers = handshakeInfo.getHeaders();
        String ip = headers.getFirst("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if(!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)){
            return null;
        } else {
            if (ip.contains(",")) {
                return ip.split(",")[0];
            } else {
                return ip;
            }
        }
    }
}
