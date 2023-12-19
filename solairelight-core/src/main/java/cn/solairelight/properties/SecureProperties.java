package cn.solairelight.properties;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class SecureProperties {
    private String metadataKey = "Metadata-Token";
    private String publicKeyBase64 = null;
    private String keyProvider = null;
}
