package cn.muskmelon.session;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Joel Ou
 */
@Getter
public class UserMetadata {
    private final Map<String, String> userRanges = new LinkedHashMap<>();
    private final Map<String, Object> userFeatures = new LinkedHashMap<>();
}
