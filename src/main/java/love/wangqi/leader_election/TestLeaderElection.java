package love.wangqi.leader_election;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 08:52
 */
public class TestLeaderElection {
    private final int SESSION_TIMEOUT = 30 * 1000;
    private final int CONNECTION_TIMEOUT = 3 * 1000;
    private final int CLIENT_NUMBER = 10;
    private static final String SERVER = "127.0.0.1:2181";
    private final String PATH = "/curator/latchPath";

    private CuratorFramework client = null;

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    List<CustomLeaderSelectorListenerAdapter> leaderSelectorListenerList = new ArrayList<>();

    @Test
    public void test() throws IOException {
        client = CuratorFrameworkFactory.newClient(SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, retryPolicy);
        client.start();

        for (int i = 0; i < CLIENT_NUMBER; i++) {
            CustomLeaderSelectorListenerAdapter leaderSelectorListener = new CustomLeaderSelectorListenerAdapter(client, PATH, "Client #" + i);
            leaderSelectorListener.start();
            leaderSelectorListenerList.add(leaderSelectorListener);
        }

        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    @After
    public void close() {
        for (CustomLeaderSelectorListenerAdapter customLeaderSelectorListenerAdapter : leaderSelectorListenerList) {
            CloseableUtils.closeQuietly(customLeaderSelectorListenerAdapter);
        }
        CloseableUtils.closeQuietly(client);
    }
}
