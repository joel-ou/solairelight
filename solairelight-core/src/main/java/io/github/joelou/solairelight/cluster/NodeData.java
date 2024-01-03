package io.github.joelou.solairelight.cluster;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joel Ou
 */
@Getter
@Setter
@Slf4j
public class NodeData implements Serializable{
    private static final long serialVersionUID = 1L;

    private final Set<String> ids = new HashSet<>();

    private final BasicInfo basicInfo = new BasicInfo();

    private final AtomicInteger sessionNumber = new AtomicInteger();

    public final static NodeData instance = new NodeData();

    private NodeData(){}

    @Getter
    @ToString
    public static class BasicInfo implements Serializable{
        private static final long serialVersionUID = 1L;

        private BasicInfo(){}

        @Setter
        private String ipAddress = ClusterTools.getLocalIPAddress();

        @Getter
        @Setter
        private String port;

        private final String nodeId = ClusterTools.getNodeId();

        //1 normal 2 loss 3 failure
        private final int status = 1;

        private long version;

        @Override
        public int hashCode() {
            return nodeId.hashCode();
        }

        public void updateVersion(){
            this.version = System.currentTimeMillis();
        }

        public String getUrl(){
            String uri = getIpAddress();
            uri = String.format("http://%s:%s", uri, getPort());
            return uri;
        }
    }

    public NodeData addID(String id){
        ids.add(id);
        return this;
    }

    public byte[] getBasicBytes() {
        return toBytes(this.basicInfo);
    }

    public byte[] getDataBytes() {
        return toBytes(this.ids);
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) return true;
        if(o instanceof NodeData){
            return ((NodeData) o).getBasicInfo().nodeId.equals(this.basicInfo.getNodeId());
        } else if (o instanceof BasicInfo){
            return ((BasicInfo) o).nodeId.equals(this.basicInfo.getNodeId());
        }
        return false;
    }

    private byte[] toBytes(Object object){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("to byte array failed.", e);
            return new byte[]{};
        }
    }

}
