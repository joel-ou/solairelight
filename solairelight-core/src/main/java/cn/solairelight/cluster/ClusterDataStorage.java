package cn.solairelight.cluster;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Joel Ou
 */
public class ClusterDataStorage {
    @Getter
    private final Set<NodeData> nodeData = new HashSet<>();

    @Getter
    private final static ClusterDataStorage instance = new ClusterDataStorage();

    private final Object basicInfoLocker = new Object();
    private final Object dataLocker = new Object();

    private ClusterDataStorage(){}

    public Stream<String> getNodeUrls(){
        return nodeData.stream().map(n->n.getBasicInfo().getNodeId());
    }

    public String findNodeUrlByUniqueID(String id){
        return nodeData.stream().parallel().flatMap(dataNode->{
            return dataNode.getIds().stream().filter(i->i.equals(id));
        }).findAny().orElse(null);
    }

    public int nodeSize(){
        return this.nodeData.size();
    }

    public void addNodeInfo(byte[] nodeInfo){
        NodeData.BasicInfo basicInfo = deserialize(nodeInfo, NodeData.BasicInfo.class);
        for (NodeData data : this.nodeData) {
            if(data.equals(basicInfo)) {
                synchronized (basicInfoLocker) {
                    data.getBasicInfo().setIpAddress(basicInfo.getIpAddress());
                }
                return;
            }
        }
    }

    public void addNodeData(byte[] ids, String nodeId){
        NodeData nodeData = deserialize(ids, NodeData.class);
        for (NodeData data : this.nodeData) {
            if(data.getBasicInfo().getNodeId().equals(nodeId)) {
                synchronized (dataLocker) {
                    data.getIds().addAll(nodeData.getIds());
                }
                return;
            }
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> tClass){
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return tClass.cast(objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void deleteNode(String nodeID){
        this.nodeData.removeIf(n->n.getBasicInfo().getNodeId().equals(nodeID));
    }
}
