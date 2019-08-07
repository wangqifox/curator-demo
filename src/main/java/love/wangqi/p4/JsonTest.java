package love.wangqi.p4;

import love.wangqi.JsonUtils;
import org.apache.curator.framework.CuratorFramework;

import java.util.UUID;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 17:15
 */

class Node {
    private String name;
    private String id;

    public Node(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

public class JsonTest extends FrameworkTest {
    final static String path = "/json";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = getClient();
        client.start();

        create(client, path + "/test1");

        Node node1 = new Node("node1", UUID.randomUUID().toString());
        String node1Str = JsonUtils.toJson(node1);

        setData(client, path + "/test1", node1Str.getBytes());

        String data = getData(client, path + "/test1");
        System.out.println(data);

    }
}
