package com.github.joelou.solairelight.session;

import com.github.joelou.solairelight.properties.SolairelightProperties;

/**
 * @author Joel Ou
 */
public class SessionBroker {

    private static CaffeineSessionStorage sessionStorage;

    public static void init(SolairelightProperties solairelightProperties) {
        if(sessionStorage != null)return;
        SessionBroker.sessionStorage = new CaffeineSessionStorage(solairelightProperties);
    }

    public static SessionStorage getStorage(){
        return sessionStorage;
    }
}
