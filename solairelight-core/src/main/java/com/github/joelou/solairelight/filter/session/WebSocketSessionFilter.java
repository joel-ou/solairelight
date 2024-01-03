package com.github.joelou.solairelight.filter.session;

import com.github.joelou.solairelight.filter.FilterContext;
import com.github.joelou.solairelight.session.BasicSession;
import com.github.joelou.solairelight.session.SessionBroker;
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
    public FilterContext<BasicSession> doFilter(FilterContext<BasicSession> filterContext) {
        BasicSession basicSession = filterContext.getPayload();

        Map<String, String> sessionHeads = basicSession.getSessionHeads();
        //handle session heads;
        HandshakeInfo handshakeInfo = basicSession.getHandshakeInfo();
        handshakeInfo.getHeaders().forEach((k,vs)-> sessionHeads.put(k, vs.get(0)));
        //set client IP
        String realIP = getClientRealIP(basicSession);
        if(realIP != null){
            basicSession.setClientIP(realIP);
        } else {
            InetSocketAddress inetSocketAddress = basicSession.getHandshakeInfo().getRemoteAddress();
            if(inetSocketAddress != null)basicSession.setClientIP(inetSocketAddress.getHostString());
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
        SessionBroker.getStorage().put(basicSession.getSessionId(), basicSession);
        return FilterContext.pass(basicSession);
    }

    @Override
    public int order() {
        return -99;
    }

    @Nullable
    private String getClientRealIP(BasicSession session){
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
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
