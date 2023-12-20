package cn.solairelight.cluster;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Ou
 */
public class ZookeeperExecutor {
    private final ZooKeeper zooKeeper;
    private final Watcher monitor;
    private final Watcher nodeMonitor;

    private final String MAIN_NODE_PATH = "/solairelight";

    private final String createAuthInfo = "solairelight:2XwnF6P2GJK4";

    private List<ACL> allPathACLs = new ArrayList<>();

    {
        try {
            String authInfo = DigestAuthenticationProvider.generateDigest(createAuthInfo);
            ACL all = new ACL(ZooDefs.Perms.ALL, new Id("digest", authInfo));
            allPathACLs.addAll(ZooDefs.Ids.READ_ACL_UNSAFE);
            allPathACLs.add(all);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ZookeeperExecutor(String hostPort, String zNode) throws IOException {
        monitor = new ZookeeperClusterMonitor(zNode);
        nodeMonitor = new ZookeeperClusterNodeMonitor(zNode);
        zooKeeper = new ZooKeeper(hostPort, 3000, monitor);
        //create auth info
        zooKeeper.addAuthInfo("digest", createAuthInfo.getBytes());
        //create nodes;
        createMainNode();
        createNode();
    }

    public void createMainNode(){
        try {
            if(this.zooKeeper.exists(MAIN_NODE_PATH, false) != null)return;
            this.zooKeeper.create(MAIN_NODE_PATH,
                    null,
                    allPathACLs,
                    CreateMode.PERSISTENT);
            this.zooKeeper.addWatch(MAIN_NODE_PATH, monitor, AddWatchMode.PERSISTENT_RECURSIVE);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNode(){
        try {
            String nodeId = ClusterTools.generateNodeId();
            String fullNodePath = MAIN_NODE_PATH+"/node";
            fullNodePath = this.zooKeeper.create(fullNodePath,
                    null,
                    allPathACLs,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            this.zooKeeper.addWatch(fullNodePath,
                    this.nodeMonitor,
                    AddWatchMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllNodes() throws InterruptedException, KeeperException {
        System.out.println(this.zooKeeper.getACL(MAIN_NODE_PATH, null));
        return this.zooKeeper.getChildren("/", false);
    }

    public void deleteAllNodes(String nodes) throws InterruptedException, KeeperException {
        this.zooKeeper.delete("/solairelight"+nodes, -1);
    }

    public void deleteMainNode(String nodes) throws InterruptedException, KeeperException {
        this.zooKeeper.delete(MAIN_NODE_PATH, -1);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZookeeperExecutor zookeeperExecutor = new ZookeeperExecutor("127.0.0.1:2181", "");
        zookeeperExecutor.deleteMainNode("");
        zookeeperExecutor.createMainNode();
        System.out.println(zookeeperExecutor.getAllNodes());
        while (true);
    }

    private List<ACL> defaultAcls(boolean isMainNode) {
        if(isMainNode){
            return new ArrayList<>(ZooDefs.Ids.READ_ACL_UNSAFE);
        } else {
            return ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
    }
}
