package io.github.joelou.solairelight.session.index;

import java.util.Set;

/**
 * @author Joel Ou
 */
public interface IndexStorage {
    enum IndexType {
        UNIQUE,
        RANGE;
    }

    void unique(String key, String value);

    void index(String key, String value);

    Set<String> get(String key);

    boolean exist(String key);

    void remove(String key);

    void removeByValue(String value);
}
