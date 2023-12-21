package cn.solairelight.cluster;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Ou
 */
@Getter
@Setter
@Slf4j
public class NodeData {

    private final Set<String> ids = new HashSet<>();

    private final BasicInfo basicInfo = new BasicInfo();

    @Getter
    public static class BasicInfo implements Serializable{
        private static final long serialVersionUID = 1L;

        @Setter
        private String ipAddress = ClusterTools.getLocalIPAddress();

        private final String nodeId = ClusterTools.getNodeId();
    }

    public NodeData addID(String id){
        ids.add(id);
        return this;
    }

    public static NodeData create(){
        return new NodeData();
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
