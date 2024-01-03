package com.github.joelou.solairelight.session;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Getter
@ToString
public class UserMetadata {
    private final Map<String, String> userRanges = new LinkedHashMap<>();
    private final Map<String, Object> userFeatures = new LinkedHashMap<>();
}
