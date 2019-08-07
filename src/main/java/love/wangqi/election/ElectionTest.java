package love.wangqi.election;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 15:48
 */
public class ElectionTest {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String PATH = "/curator/election";

    public static void main(String[] args) throws IOException {
        String name = args[0];
        System.out.println("start " + name);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(60 * 1000)
                .namespace("arch")
                .build();
        client.start();



        CustomLeaderSelectorListenerAdapter leaderSelectorListener = new CustomLeaderSelectorListenerAdapter(client, PATH, "Client " + name);
        leaderSelectorListener.start();

        new BufferedReader(new InputStreamReader(System.in)).readLine();

        CloseableUtils.closeQuietly(leaderSelectorListener);

    }
}
