package cn.muskmelon.filter.session;

import cn.muskmelon.filter.FilterCargo;
import cn.muskmelon.session.BasicSession;
import cn.muskmelon.session.SessionBroker;
import cn.muskmelon.session.WebSocketSessionExpand;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
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
    public FilterCargo<BasicSession> execute(FilterCargo<?> filterCargo) {
        Object payload = filterCargo.getPayload();
        WebSocketSessionExpand socketSessionExpand = (WebSocketSessionExpand) payload;
        socketSessionExpand.setServiceId("0");

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
        return FilterCargo.pass(socketSessionExpand);
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
