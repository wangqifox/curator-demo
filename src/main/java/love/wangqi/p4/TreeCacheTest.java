package love.wangqi.p4;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 15:08
 */
public class TreeCacheTest extends FrameworkTest {
    public static void main(String[] args) throws Exception {
        final String path = "/treeChildrenCache";
        final CuratorFramework client = getClient();
        client.start();

        final TreeCache cache = new TreeCache(client, path);
        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED: " + event.getData().getPath());
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED: " + event.getData().getPath());
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE_UPDATED: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
                        break;
                    case CONNECTION_LOST:
                        System.out.println("CONNECTION_LOST: " + event.getData().getPath());
                        break;
                    case CONNECTION_RECONNECTED:
                        System.out.println("CONNECTION_RECONNECTED: " + event.getData().getPath());
                        break;
                    case CONNECTION_SUSPENDED:
                        System.out.println("CONNECTION_SUSPENDED: " + event.getData().getPath());
                        break;
                    case INITIALIZED:
                        System.out.println("INITIALIZED: " + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });
        cache.start();

        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);

        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1");
        Thread.sleep(1000);

        setData(client, path, "test".getBytes());
        Thread.sleep(1000);

        client.delete().forPath(path + "/c1");
        Thread.sleep(1000);

        client.delete().forPath(path);
        Thread.sleep(1000);

        client.close();
    }
}
