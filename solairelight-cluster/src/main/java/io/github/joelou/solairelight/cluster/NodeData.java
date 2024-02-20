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
import java.util.Objects;
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

    private transient final Set<String> ids = new HashSet<>();

    private final BasicInfo basicInfo = new BasicInfo();

    private final AtomicInteger sessionQuota = new AtomicInteger();

    @Setter
    private int maxSessionNumber = 0;

    private long version;

    public final static NodeData instance = new NodeData();

    private NodeData(){}

    @Getter
    @ToString
    public static class BasicInfo implements Serializable{
        private static final long serialVersionUID = 1L;

        private BasicInfo(){}

        private final String ipAddress = ClusterTools.getLocalIPAddress();

        @Setter
        private String port;

        private final String nodeId = ClusterTools.getNodeId();

        //1 normal 2 unhealthy 3 failed
        private final AtomicInteger status = new AtomicInteger(1);

        @Override
        public int hashCode() {
            return nodeId.hashCode();
        }


        public String getUrl(){
            String uri = getIpAddress();
            uri = String.format("http://%s:%s", uri, getPort());
            return uri;
        }

        AtomicInteger getStatus() {
            return status;
        }
    }

    void updateVersion(){
        this.version = System.currentTimeMillis();
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
            return ((NodeData) o).getBasicInfo().getNodeId().equals(this.basicInfo.getNodeId());
        } else if (o instanceof BasicInfo){
            return ((BasicInfo) o).getNodeId().equals(this.basicInfo.getNodeId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicInfo.nodeId);
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
