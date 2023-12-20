package cn.solairelight.cluster;

import lombok.Getter;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;

/**
 * @author Joel Ou
 */
@Getter
public class ZookeeperClusterMonitor implements ClusterMonitor, Watcher, AsyncCallback.StatCallback {
    private ZooKeeper zooKeeper;
    private final String zNode;
    private byte[] prevData;

    public ZookeeperClusterMonitor(String zNode) {
        this.zNode = zNode;
        // Get things started by checking if the node exists. We are going
        // to be completely event driven
//        zooKeeper.exists(zNode, true, this, null);
    }

    /**
     * Other classes use the DataMonitor by implementing this method
     */
    public interface DataMonitorListener {
        /**
         * The existence status of the node has changed.
         */
        void exists(byte[] data);

        /**
         * The ZooKeeper session is no longer valid.
         *
         * @param rc
         *                the ZooKeeper reason code
         */
        void closing(int rc);
    }

    public void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    // In this particular example we don't need to do anything
                    // here - watches are automatically re-registered with
                    // server and any watches triggered while the client was
                    // disconnected will be delivered (in order of course)
                    break;
                case Expired:
                    // It's all over
//                    listener.closing(KeeperException.Code.SessionExpired);
                    break;
            }
        } else {
            if (path != null && path.equals(zNode)) {
                // Something has changed on the node, let's find out
                zooKeeper.exists(zNode, true, this, null);
            }
        }
    }

    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;
        switch (rc) {
            case KeeperException.Code.Ok:
                exists = true;
                break;
            case KeeperException.Code.NoNode:
                exists = false;
                break;
            case KeeperException.Code.SessionExpired:
            case KeeperException.Code.NoAuth:
                return;
            default:
                // Retry errors
                zooKeeper.exists(zNode, true, this, null);
                return;
        }

        byte[] b = null;
        if (exists) {
            try {
                b = zooKeeper.getData(zNode, false, null);
            } catch (KeeperException e) {
                // We don't need to worry about recovering now. The watch
                // callbacks will kick off any exception handling
                e.printStackTrace();
            } catch (InterruptedException e) {
                return;
            }
        }
        if ((b == null && b != prevData)
                || (b != null && !Arrays.equals(prevData, b))) {
            prevData = b;
        }
    }
}
