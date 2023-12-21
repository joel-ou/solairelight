package cn.solairelight.cluster;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Joel Ou
 */
@Getter
@Slf4j
public class ZookeeperClusterMonitor implements Watcher, AsyncCallback.StatCallback {
    @Setter
    private ZooKeeper zooKeeper;

    private static final Pattern nodeIdPattern = Pattern.compile("^.+node-(.+)-.+$");

    @Override
    public void process(WatchedEvent event) {
        if(event.getType() == Event.EventType.None)return;
        boolean isDataNode = event.getPath().contains("data");
        String nodeId = getNodeIdByPath(event.getPath());
        switch (event.getType()) {
            case NodeCreated:
            case NodeDataChanged:
                addData(isDataNode, nodeId, event);
                break;
            case NodeDeleted:
                if(!isDataNode){
                    ClusterDataStorage.getInstance().deleteNode(nodeId);
                }
                break;
        }
        log.info("a event triggered. {}", event);
    }

    /**
     * callback of the exists()
     * @param rc   The return code or the result of the call.
     * @param path The path that we passed to asynchronous calls.
     * @param ctx  Whatever context object that we passed to asynchronous calls.
     * @param stat {@link Stat} object of the node on given path.
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        log.info("stat changed. rc:{}, path:{}, stat:{}", rc, path, stat);
    }

    private String getNodeIdByPath(String path){
        Matcher matcher = nodeIdPattern.matcher(path);
        return matcher.find()?matcher.group(1):null;
    }

    private void addData(boolean isDataNode, String nodeId, WatchedEvent event){
        try {
            byte[] bytes = this.zooKeeper.getData(event.getPath(), false, null);
            if(isDataNode){
                ClusterDataStorage.getInstance().addNodeData(bytes, nodeId);
            } else {
                ClusterDataStorage.getInstance().addNodeInfo(bytes);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("add node data failed.", e);
            throw new RuntimeException(e);
        }
    }
}
