package discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 17:46
 */
public class Discovery {
    private Map<String, Node> nodeMap = new HashMap<>();

    final CuratorFramework client;
    final TreeCache cache;
    final String path;

    public Discovery(CuratorFramework client, String path) throws Exception {
        this.client = client;
        this.path = path;
        cache = new TreeCache(client, path);
        init();
    }

    private void init() throws Exception {
        List<String> nodeNames = client.getChildren().forPath(path);
        for (String nodeName : nodeNames) {
            Node node = Node.fromString(new String(client.getData().forPath(path + "/" + nodeName)));
            nodeMap.put(nodeName, node);
        }
    }

    public void start() throws Exception {
        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                Node node;
                switch (event.getType()) {
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED: " + event.getData().getPath());
                        node = Node.fromString(new String(event.getData().getData()));
                        nodeMap.put(node.getName(), node);
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED: " + event.getData().getPath());
                        node = Node.fromString(new String(event.getData().getData()));
                        nodeMap.remove(node.getName());
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE_UPDATED: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
                        node = Node.fromString(new String(event.getData().getData()));
                        nodeMap.put(node.getName(), node);
                        break;
                    default:
                        break;
                }
            }
        });
        cache.start();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("=========== nodes info =========");
                nodeMap.forEach((k, v) -> {
                    System.out.println(k + " " + v.toString());
                });
            }
        }, 5, 3, TimeUnit.SECONDS);
    }


}
