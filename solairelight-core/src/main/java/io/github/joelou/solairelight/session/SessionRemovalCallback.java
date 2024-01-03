package io.github.joelou.solairelight.session;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Joel Ou
 */
@Slf4j
public class SessionRemovalCallback implements RemovalListener<String, BasicSession> {

    @Override
    public void onRemoval(@Nullable String key, @Nullable BasicSession session, RemovalCause cause) {
        log.debug("session cache removed. reason: {}", cause);
        if(session == null || session.isClosed()){
            return;
        }
        session.close();
    }
}
