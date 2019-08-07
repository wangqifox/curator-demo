package love.wangqi.p1;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 10:13
 */
public class LeaderSelectorZkClient {
    private static final int CLIENT_QTY = 10;
    private static final String ZOOKEEPER_SERVER = "127.0.0.1:2181";

    public static void main(String[] args) {
        List<ZkClient> clients = new ArrayList<>();
        List<WorkServer> workServers = new ArrayList<>();

        try {
            for (int i = 0; i < CLIENT_QTY; i++) {
                ZkClient client = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new SerializableSerializer());
                clients.add(client);

                RunningData runningData = new RunningData();
                runningData.setCid(Long.valueOf(i));
                runningData.setName("Client #" + i);

                WorkServer workServer = new WorkServer(runningData);
                workServer.setZkClient(client);

                workServers.add(workServer);
                workServer.start();
            }
            System.out.println("敲回车键退出!\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            System.out.println("Shutting down...");
            for (WorkServer workServer : workServers) {
                try {
                    workServer.stop();
                } catch (Exception er) {
                    er.printStackTrace();
                }
                for (ZkClient client : clients) {
                    client.close();
                }
            }
        }
    }
}
