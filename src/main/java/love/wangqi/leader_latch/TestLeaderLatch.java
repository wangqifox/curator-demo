package love.wangqi.leader_latch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-07-31 21:08
 */
public class TestLeaderLatch {
    private final int SESSION_TIMEOUT = 30 * 1000;
    private final int CONNECTION_TIMEOUT = 3 * 1000;
    private final int CLIENT_NUMBER = 10;
    private static final String SERVER = "127.0.0.1:2181";
    private final String PATH = "/curator/latchPath";

    private CuratorFramework client = null;

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    List<LeaderLatch> leaderLatchList = new ArrayList<>(CLIENT_NUMBER);

    @Before
    public void init() throws Exception {
        client = CuratorFrameworkFactory.newClient(SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, retryPolicy);
        client.start();

        for (int i = 0; i < CLIENT_NUMBER; i++) {
            LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "Client #" + i);
            leaderLatchList.add(leaderLatch);
            leaderLatch.start();
        }

        TimeUnit.SECONDS.sleep(5);
        System.out.println("*********LeaderLatch初始化完成*********");
    }

    @Test
    public void testCheckLeader() throws Exception {
        LeaderLatch currentLeader = null;

        for (LeaderLatch leaderLatch : leaderLatchList) {
            if (leaderLatch.hasLeadership()) {
                currentLeader = leaderLatch;
                break;
            }
        }

        System.out.println("当前leader是：" + currentLeader.getId());

        currentLeader.close();
        leaderLatchList.remove(currentLeader);
        TimeUnit.SECONDS.sleep(5);

        for (LeaderLatch leaderLatch : leaderLatchList) {
            if (leaderLatch.hasLeadership()) {
                currentLeader = leaderLatch;
                break;
            }
        }

        System.out.println("新leader是：" + currentLeader.getId());
        currentLeader.close();
        leaderLatchList.remove(currentLeader);

        LeaderLatch firstNode = leaderLatchList.get(0);
        System.out.println("删除leader后，当前第一个节点：" + firstNode.getId());

        firstNode.await(10, TimeUnit.SECONDS);

        for (LeaderLatch leaderLatch : leaderLatchList) {
            if (leaderLatch.hasLeadership()) {
                currentLeader = leaderLatch;
                break;
            }
        }

        System.out.println("最终实际leader是：" + currentLeader.getId());
    }

    @After
    public void close() {
        for (LeaderLatch leaderLatch : leaderLatchList) {
            CloseableUtils.closeQuietly(leaderLatch);
        }
        CloseableUtils.closeQuietly(client);
    }

}
