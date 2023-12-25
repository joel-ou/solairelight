package cn.solairelight.filter.session;

import cn.solairelight.cluster.SolairelightRedisClient;
import cn.solairelight.filter.FilterContext;
import cn.solairelight.properties.SecureProperties;
import cn.solairelight.properties.SolairelightProperties;
import cn.solairelight.session.BasicSession;
import cn.solairelight.session.WebSocketSessionExpand;
import cn.solairelight.session.index.IndexService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Slf4j
@Component
public class UserMetadataFilter implements SessionFilter {
    @Resource
    private IndexService indexService;
    @Resource
    private SolairelightProperties solairelightProperties;

    @Override
    public FilterContext<BasicSession> execute(FilterContext<?> filterContext) {
        SecureProperties secureProperties = solairelightProperties.getSecure();

        Object payload = filterContext.getPayload();
        WebSocketSessionExpand socketSessionExpand = (WebSocketSessionExpand) payload;
        Object metadataToken = socketSessionExpand.getSessionHeads().get(secureProperties.getMetadataKey());
        if(metadataToken == null){
            return FilterContext.pass();
        }

        //parsing metadataToken.
        String jwtToken = metadataToken.toString();
        Claims claims = Jwts.parser()
            .keyLocator(key-> stringToPublic(key.getAlgorithm(), secureProperties.getPublicKeyBase64()))
            .build()
            .parseSignedClaims(jwtToken)
            .getPayload();

        Object userRangeObj = claims.get("userRanges");
        if(userRangeObj instanceof Map){
            //store indexes
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) userRangeObj).entrySet()) {
                String k=entry.getKey().toString(), v=entry.getValue().toString();
                socketSessionExpand.getUserMetadata().getUserRanges().put(k, v);
                indexService.index(k, v, socketSessionExpand.getSessionId());

                //store ids
                //TODO
//                if(k.equals("id")) {
//                    SolairelightRedisClient.getInstance().pushId(v);
//                }
            }
        }

        Object userFeatureObj = claims.get("userFeatures");
        if(userFeatureObj instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) userFeatureObj).entrySet()) {
                socketSessionExpand
                        .getUserMetadata()
                        .getUserFeatures()
                        .put(entry.getKey().toString(), entry.getValue());
            }
        }
        return FilterContext.pass(socketSessionExpand);
    }

    public static PublicKey stringToPublic(String algorithm, String publicKeyString) {
        if (algorithm.startsWith("RS")){
            algorithm = "RSA";
        }else if(algorithm.startsWith("ES")){
            algorithm = "EC";
        } else {
            throw new RuntimeException("unsupported jwt algorithm.");
        }
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("convert string to publicKey failed.", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int order() {
        return -97;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = Jwts.SIG.RS512.keyPair().build().getPublic();
        String publicKeyString = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEVs/o5+uQbTjL3chynL4wXgUg2R9q9UU8I5mEovUf86QZ7kOBIjJwqnzD1omageEHWwHdBO6B+dFabmdT9POxg==";
        System.out.println(publicKeyString);

        System.out.println(stringToPublic("EC", publicKeyString));
    }
}
