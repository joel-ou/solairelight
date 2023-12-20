package cn.solairelight.cluster;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Joel Ou
 */
public class NodeData extends HashMap<String, Object> implements Serializable {
    private final static String IP_ADDRESS_KEY = "ip_address";

    public void putIPAddress(String ip){
        super.put(IP_ADDRESS_KEY, ip);
    }
    public String getIpAddress(String ip){
       return super.getOrDefault(IP_ADDRESS_KEY, "").toString();
    }

    public NodeData put(String key, Object val){
        super.put(key, val);
        return this;
    }

    public static NodeData create(){
        return new NodeData();
    }

    public byte[] toJsonByte() {
        return new byte[]{};
    }
}
