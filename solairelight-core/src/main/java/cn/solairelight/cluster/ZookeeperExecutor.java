package cn.solairelight.cluster;

import cn.solairelight.properties.SolairelightZookeeperProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Joel Ou
 */
@Slf4j
public class ZookeeperExecutor {
    private final ZooKeeper zooKeeper;

    private final Watcher monitor;

    private final String MAIN_NODE_PATH = "/solairelight";

    @Setter
    private String createAuthInfo = "solairelight:2XwnF6P2GJK4";

    private final List<ACL> allPathACLs = new ArrayList<>();

    @Getter
    private static ZookeeperExecutor instance;

    {
        try {
            String authInfo = DigestAuthenticationProvider.generateDigest(createAuthInfo);
            ACL all = new ACL(ZooDefs.Perms.ALL, new Id("digest", authInfo));
            allPathACLs.addAll(ZooDefs.Ids.READ_ACL_UNSAFE);
            allPathACLs.add(all);
        } catch (NoSuchAlgorithmException e) {
            log.error("init zookeeperExecutor failed.", e);
            throw new RuntimeException(e);
        }
    }

    public static void init(SolairelightZookeeperProperties zookeeper) {
        instance = new ZookeeperExecutor(zookeeper.getHostPort());
        if(StringUtils.hasText(zookeeper.getAuthInfo())) {
            instance.setCreateAuthInfo(zookeeper.getAuthInfo());
        }
    }

    public ZookeeperExecutor(String hostPort) {
        monitor = new ZookeeperClusterMonitor();
        try {
            zooKeeper = new ZooKeeper(hostPort, 3000, monitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //create auth info
        zooKeeper.addAuthInfo("digest", createAuthInfo.getBytes());
        //create paths;
//        createMainPath();
//        createChildPath();
    }

    public void createMainPath(){
        try {
            if(this.zooKeeper.exists(MAIN_NODE_PATH, false) == null){
                byte[] nodeDate = String.format("created by node %s at %s", ClusterTools.getNodeId(), LocalDateTime.now()).getBytes();
                this.zooKeeper.create(MAIN_NODE_PATH,
                        nodeDate,
                        allPathACLs,
                        CreateMode.PERSISTENT);
            }
//            this.zooKeeper.addWatch(MAIN_NODE_PATH, monitor, AddWatchMode.PERSISTENT_RECURSIVE);
        } catch (KeeperException | InterruptedException e) {
            log.error("create main path failed.", e);
            throw new RuntimeException(e);
        }
    }

    public void createChildPath(){
        try {
            String fullNodePath = String.format("%s/node-%s-", MAIN_NODE_PATH, ClusterTools.getNodeId());
            fullNodePath = this.zooKeeper.create(fullNodePath,
                    NodeData.create().getBasicBytes(),
                    allPathACLs,
                    CreateMode.PERSISTENT_WITH_TTL,
                    null,
                    10000L);
            this.zooKeeper.addWatch(fullNodePath, this.monitor, AddWatchMode.PERSISTENT_RECURSIVE);

            //creating date path
            this.zooKeeper.create(fullNodePath+"/data", new byte[]{}, allPathACLs, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            log.error("create child path failed.", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllChildes() {
        try {
            return this.zooKeeper.getChildren(MAIN_NODE_PATH, false);
        } catch (Exception e) {
            log.error("error occurred when get child paths.", e);
            return Collections.emptyList();
        }
    }

    public void delete() throws InterruptedException, KeeperException {
        this.zooKeeper.delete("/solairelight/node-367BF6F970648212-0000000012", -1);
        this.zooKeeper.delete("/solairelight/node-367BF6F970645265-0000000013", -1);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZookeeperExecutor executor = new ZookeeperExecutor("127.0.0.1:2181");
//        executor.createChildPath();
//        executor.delete();
        System.out.println(executor.getAllChildes());
//        zookeeperExecutor.deleteMainNode("");
//        zookeeperExecutor.createMainPath();
    }
}
