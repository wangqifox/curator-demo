package love.wangqi.latch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 16:11
 */
public class LatchTest {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String PATH = "/curator/latch";

    public static void main(String[] args) throws Exception {
        String name = args[0];
        System.out.println("start " + name);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(5 * 1000)
                .namespace("arch")
                .build();
        client.start();

        LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "client " + name);
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println(leaderLatch.getId() + " is leader");
            }

            @Override
            public void notLeader() {
                System.out.println(leaderLatch.getId() + " is not leader");
            }
        });
        leaderLatch.start();

        new BufferedReader(new InputStreamReader(System.in)).readLine();

        System.out.println("exist...");
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(leaderLatch);
    }
}
