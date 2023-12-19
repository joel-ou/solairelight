package cn.muskmelon.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Joel Ou
 */
@Component
public class SessionBroker {

    private static CaffeineSessionStorage sessionStorage;

    private SessionBroker() {
    }

    @Autowired
    public SessionBroker(CaffeineSessionStorage sessionStorage) {
        SessionBroker.sessionStorage = sessionStorage;
    }

    public static SessionStorage getStorage(){
        return sessionStorage;
    }
}
