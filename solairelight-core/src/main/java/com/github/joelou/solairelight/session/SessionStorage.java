package com.github.joelou.solairelight.session;

import java.util.Collection;
import java.util.Set;

/**
 * @author Joel Ou
 */
public interface SessionStorage {

    void put(String key, BasicSession session);

    BasicSession get(String key);

    Collection<BasicSession> getAll();

    public Set<BasicSession> getAll(Set<String> keys);

    void invalidate(String key);

}
