package love.wangqi.p4;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 13:52
 */
public class FrameworkTest {
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    public static CuratorFramework getClient() {
        return CuratorFrameworkFactory.builder()
                .connectString(ZK_ADDRESS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(60 * 1000)
                .namespace("arch")
                .build();
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework client = getClient();
        client.start();

        System.out.println(getChildren(client, "/"));

        create(client, "/zktest/test", "hello".getBytes());
        System.out.println(getData(client, "/zktest/test"));
        setData(client, "/zktest/test", "world".getBytes());
        System.out.println(getData(client, "/zktest/test"));
        TimeUnit.SECONDS.sleep(10);
        delete(client, "/zktest/test");

        createEphemeral(client, "/zktest/test", "hello".getBytes());

        for (int i = 0; i < 100; i++) {
            createEphemeralSequential(client, "/zktest/test1", "hello".getBytes());
        }
        TimeUnit.SECONDS.sleep(10);
    }

    public static void create(final CuratorFramework client, final String path) throws Exception {
        client.create().creatingParentsIfNeeded().forPath(path);
    }

    public static void create(final CuratorFramework client, final String path, final byte[] payload) throws Exception {
        client.create().creatingParentsIfNeeded().forPath(path, payload);
    }

    public static void createEphemeral(final CuratorFramework client, final String path, final byte[] payload) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
    }

    public static String createEphemeralSequential(final CuratorFramework client, final String path, final byte[] payload) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, payload);
    }

    public static void setData(final CuratorFramework client, final String path, final byte[] payload) throws Exception {
        client.setData().forPath(path, payload);
    }

    public static void delete(final CuratorFramework client, final String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public static void guaranteedDelete(final CuratorFramework client, final String path) throws Exception {
        client.delete().guaranteed().forPath(path);
    }

    public static String getData(final CuratorFramework client, final String path) throws Exception {
        return new String(client.getData().forPath(path));
    }

    public static List<String> getChildren(final CuratorFramework client, final String path) throws Exception {
        return client.getChildren().forPath(path);
    }
}
