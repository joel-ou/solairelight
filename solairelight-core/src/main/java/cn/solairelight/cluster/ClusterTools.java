package cn.solairelight.cluster;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

/**
 * @author Joel Ou
 */
@Slf4j
public class ClusterTools {

    private static long DEFAULT_CUSTOM_EPOCH = 1703030400000L;
    private static final Random random = new Random();

    private static String NODE_ID;

    public static String getNodeId() {
        return getNodeId(true);
    }
    public static synchronized String getNodeId(boolean noRandom) {
        if(NODE_ID != null){return NODE_ID;}
        if(noRandom) return NODE_ID = getMAC();
        NODE_ID = getMAC()+String.format("%04d", random.nextInt(9999));
        return NODE_ID;
    }

    public boolean isStandalone() {
        String env = System.getenv().getOrDefault("standalone", String.valueOf(false));
        return Boolean.parseBoolean(env);
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
