package cn.muskmelon.filter.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import cn.muskmelon.filter.FilterCargo;
import cn.muskmelon.properties.MuskmelonProperties;
import cn.muskmelon.properties.SecureProperties;
import cn.muskmelon.session.BasicSession;
import cn.muskmelon.session.WebSocketSessionExpand;
import cn.muskmelon.session.index.IndexService;
import org.springframework.stereotype.Component;

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
    private MuskmelonProperties muskmelonProperties;

    @Override
    public FilterCargo<BasicSession> execute(FilterCargo<?> filterCargo) {
        SecureProperties secureProperties = muskmelonProperties.getSecure();

        Object payload = filterCargo.getPayload();
        WebSocketSessionExpand socketSessionExpand = (WebSocketSessionExpand) payload;
        Object metadataToken = socketSessionExpand.getSessionHeads().get(secureProperties.getMetadataKey());
        if(metadataToken == null){
            return FilterCargo.pass();
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
        return FilterCargo.pass(socketSessionExpand);
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
