package love.wangqi.p4;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 14:41
 */
public class PathCacheTest extends FrameworkTest {
    public static void main(String[] args) throws Exception {
        final String path = "/pathChildrenCache";
        final CuratorFramework client = getClient();
        client.start();

        final PathChildrenCache cache = new PathChildrenCache(client, path, true);

        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED:" + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED:" + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED:" + event.getData().getPath() + " " + new String(event.getData().getData()));
                        break;
                    case CONNECTION_LOST:
                        System.out.println("CONNECTION_LOST:" + event.getData().getPath());
                        break;
                    case CONNECTION_RECONNECTED:
                        System.out.println("CONNECTION_RECONNECTED:" + event.getData().getPath());
                        break;
                    case CONNECTION_SUSPENDED:
                        System.out.println("CONNECTION_SUSPENDED:" + event.getData().getPath());
                        break;
                    case INITIALIZED:
                        System.out.println("INITIALIZED:" + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

//        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);

        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1");
        Thread.sleep(1000);

        client.setData().forPath(path + "/c1", "hello".getBytes());
        Thread.sleep(1000);

        client.setData().forPath(path, "hello".getBytes());
        Thread.sleep(1000);

        client.delete().forPath(path + "/c1");
        Thread.sleep(1000);

        // 监听节点本身的变化不会通知
        client.delete().forPath(path);
        Thread.sleep(1000);

        client.close();
    }
}
