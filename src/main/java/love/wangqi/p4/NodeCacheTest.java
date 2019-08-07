package love.wangqi.p4;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 14:23
 */
public class NodeCacheTest extends FrameworkTest {

    public static void main(String[] args) throws Exception {
        final String path = "/nodeCache";
        final CuratorFramework client = getClient();
        client.start();

        try {
            delete(client, path);
        } catch (Exception ignore) {
//            ignore.printStackTrace();
        }

        create(client, path, "cache".getBytes());

        final NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println(new String(nodeCache.getCurrentData().getData()));
            }
        });
        nodeCache.start(true);

        setData(client, path, "cache1".getBytes());
        setData(client, path, "cache2".getBytes());
        createEphemeral(client, path + "/test1", "hello".getBytes());

        Thread.sleep(1000);

        client.close();

    }
}
