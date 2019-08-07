package discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 17:47
 */
public class DiscoveryService {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String ELECTION_PATH = "/discovery/election";
    private static final String NODE_PATH = "/discovery/nodes";

    public static void main(String[] args) throws Exception {
        String name = args[0];
        System.out.println("start " + name);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(3 * 1000)
                .namespace("arch")
                .build();
        client.start();

        // 注册节点信息
        Node node = new Node(name, UUID.randomUUID().toString());
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(NODE_PATH + "/" + name, node.toString().getBytes());

        Discovery discovery = new Discovery(client, NODE_PATH);
        discovery.start();

        // 注册节点选举
//        LeaderLatch leaderLatch = new LeaderLatch(client, ELECTION_PATH, name);
//        leaderLatch.addListener(new LeaderLatchListener() {
//            @Override
//            public void isLeader() {
//                System.out.println(leaderLatch.getId() + " is leader");
//                // 改变节点的信息
//                try {
//                    Node node = Node.fromString(new String(client.getData().forPath(NODE_PATH + "/" + name)));
//                    node.setState(Node.MASTER);
//                    client.setData().forPath(NODE_PATH + "/" + name, node.toString().getBytes());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void notLeader() {
//                System.out.println(leaderLatch.getId() + " is not leader");
//                // 改变节点的信息
//                try {
//                    Node node = Node.fromString(new String(client.getData().forPath(NODE_PATH + "/" + name)));
//                    node.setState(Node.DATA);
//                    client.setData().forPath(NODE_PATH + "/" + name, node.toString().getBytes());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        leaderLatch.start();

        new BufferedReader(new InputStreamReader(System.in)).readLine();

        System.out.println("exist...");
        CloseableUtils.closeQuietly(client);
//        CloseableUtils.closeQuietly(leaderLatch);
    }
}
