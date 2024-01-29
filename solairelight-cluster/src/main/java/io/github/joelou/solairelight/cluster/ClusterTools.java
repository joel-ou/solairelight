package io.github.joelou.solairelight.cluster;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Random;

/**
 * @author Joel Ou
 */
@Slf4j
public class ClusterTools {
    private static final Random random = new Random();

    public final static String SOLAIRELIGHTS_SERVICE_ID = "solairelight";
    public final static String SOLAIRELIGHTS_SESSION_QUOTA_KEY = "sessionQuota";

    private static String NODE_ID;

    public static String getNodeId() {
        return NODE_ID;
    }

    public static void initNodeId(String suffix) {
        initNodeId(true, suffix);
    }

    public static synchronized void initNodeId(boolean noRandom, String suffix) {
        if(NODE_ID != null){return;}
        String suffixStr = Optional.ofNullable(suffix).orElse("");
        if(noRandom) {
            NODE_ID = getMAC() + suffixStr;
            return;
        }
        NODE_ID = getMAC()+String.format("%04d", random.nextInt(9999))+suffixStr;
    }

    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) {
                NetworkInterface face = faces.nextElement();
                if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addressEnumeration = face.getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    InetAddress address = addressEnumeration.nextElement();
                    if (!address.isLoopbackAddress()
                            && address.isSiteLocalAddress()
                            && !address.isAnyLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("get local ip failed", e);
        }
        return null;
    }

    private static String getMAC(){
        try {
            byte[] mac = null;
            Enumeration<NetworkInterface> networkInterfaces =  NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                mac = networkInterface.getHardwareAddress();
                if(!networkInterface.isLoopback() && mac != null){
                    break;
                }
            }
            assert mac != null;
            StringBuilder sb = new StringBuilder();
            for (byte b : mac) {
                String s =  String.format("%02X", b);
                sb.append(s);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("getMAC failed.", e);
            throw new RuntimeException(e);
        }
    }
}
