package io.github.joelou.solairelight.runner.controller;

import io.github.joelou.solairelight.SolairelightSettings;
import io.github.joelou.solairelight.cluster.ClusterTools;
import io.github.joelou.solairelight.properties.SolairelightProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

/**
 * @author Joel Ou
 */
@RestController
@RequestMapping("session")
public class SessionController {

    @Resource
    private LoadBalancerClientFactory clientFactory;
    @Resource
    private SolairelightProperties solairelightProperties;

    private final byte[] keyBytes = Base64Utils.decode(("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC7VJTUt9Us8cKj" +
                "MzEfYyjiWA4R4/M2bS1GB4t7NXp98C3SC6dVMvDuictGeurT8jNbvJZHtCSuYEvu" +
                "NMoSfm76oqFvAp8Gy0iz5sxjZmSnXyCdPEovGhLa0VzMaQ8s+CLOyS56YyCFGeJZ" +
                "qgtzJ6GR3eqoYSW9b9UMvkBpZODSctWSNGj3P7jRFDO5VoTwCQAWbFnOjDfH5Ulg" +
                "p2PKSQnSJP3AJLQNFNe7br1XbrhV//eO+t51mIpGSDCUv3E0DDFcWDTH9cXDTTlR" +
                "ZVEiR2BwpZOOkE/Z0/BVnhZYL71oZV34bKfWjQIt6V/isSMahdsAASACp4ZTGtwi" +
                "VuNd9tybAgMBAAECggEBAKTmjaS6tkK8BlPXClTQ2vpz/N6uxDeS35mXpqasqskV" +
                "laAidgg/sWqpjXDbXr93otIMLlWsM+X0CqMDgSXKejLS2jx4GDjI1ZTXg++0AMJ8" +
                "sJ74pWzVDOfmCEQ/7wXs3+cbnXhKriO8Z036q92Qc1+N87SI38nkGa0ABH9CN83H" +
                "mQqt4fB7UdHzuIRe/me2PGhIq5ZBzj6h3BpoPGzEP+x3l9YmK8t/1cN0pqI+dQwY" +
                "dgfGjackLu/2qH80MCF7IyQaseZUOJyKrCLtSD/Iixv/hzDEUPfOCjFDgTpzf3cw" +
                "ta8+oE4wHCo1iI1/4TlPkwmXx4qSXtmw4aQPz7IDQvECgYEA8KNThCO2gsC2I9PQ" +
                "DM/8Cw0O983WCDY+oi+7JPiNAJwv5DYBqEZB1QYdj06YD16XlC/HAZMsMku1na2T" +
                "N0driwenQQWzoev3g2S7gRDoS/FCJSI3jJ+kjgtaA7Qmzlgk1TxODN+G1H91HW7t" +
                "0l7VnL27IWyYo2qRRK3jzxqUiPUCgYEAx0oQs2reBQGMVZnApD1jeq7n4MvNLcPv" +
                "t8b/eU9iUv6Y4Mj0Suo/AU8lYZXm8ubbqAlwz2VSVunD2tOplHyMUrtCtObAfVDU" +
                "AhCndKaA9gApgfb3xw1IKbuQ1u4IF1FJl3VtumfQn//LiH1B3rXhcdyo3/vIttEk" +
                "48RakUKClU8CgYEAzV7W3COOlDDcQd935DdtKBFRAPRPAlspQUnzMi5eSHMD/ISL" +
                "DY5IiQHbIH83D4bvXq0X7qQoSBSNP7Dvv3HYuqMhf0DaegrlBuJllFVVq9qPVRnK" +
                "xt1Il2HgxOBvbhOT+9in1BzA+YJ99UzC85O0Qz06A+CmtHEy4aZ2kj5hHjECgYEA" +
                "mNS4+A8Fkss8Js1RieK2LniBxMgmYml3pfVLKGnzmng7H2+cwPLhPIzIuwytXywh" +
                "2bzbsYEfYx3EoEVgMEpPhoarQnYPukrJO4gwE2o5Te6T5mJSZGlQJQj9q4ZB2Dfz" +
                "et6INsK0oG8XVGXSpQvQh3RUYekCZQkBBFcpqWpbIEsCgYAnM3DQf3FJoSnXaMhr" +
                "VBIovic5l0xFkEHskAjFTevO86Fsz1C2aSeRKSqGFoOQ0tmJzBEs1R6KqnHInicD" +
                "TQrKhArgLXX4v3CddjfTRJkFWDbE/CkvKZNOrcf1nhaGCPspRJj2KUkj1Fhl9Cnc" +
                "dn/RsYEONbwQSjIfMPkvxF+8HQ==").getBytes());

    @PostMapping("/token")
    public Mono<NewSessionResponse> createToken(@RequestBody Map<String, Object> userMetadata) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        Key privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);

        NewSessionResponse newSessionResponse = new NewSessionResponse();
        String token = Jwts.builder()
                .claims(userMetadata)
                .expiration(Date.from(LocalDateTime.now().atZone(ZoneOffset.systemDefault()).plusMinutes(1).toInstant()))
                .signWith(privateKey)
                .compact();
        newSessionResponse.setToken(token);
        if(SolairelightSettings.isCluster()){
            ReactorLoadBalancer<ServiceInstance> loadBalancer = this.clientFactory.getInstance(ClusterTools.SOLAIRELIGHTS_SERVICE_ID,
                    ReactorServiceInstanceLoadBalancer.class);
            return loadBalancer.choose().map(instance->{
                if(instance.hasServer()) {
                    URI uri = instance.getServer().getUri();
                    String instanceUri = String.format("%s%s%s", uri.getHost(), uri.getPort()>0?":"+uri.getPort():uri.getPort(),
                            solairelightProperties.getWebsocket().getPath());
                    newSessionResponse.setInstance(instanceUri);
                }
                return newSessionResponse;
            });
        } else {
            newSessionResponse.setInstance(null);
        }
        return Mono.just(newSessionResponse);
    }
}
