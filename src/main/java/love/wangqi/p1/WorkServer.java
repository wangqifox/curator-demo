package love.wangqi.p1;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 09:39
 */
public class WorkServer {
    private volatile boolean running = false;
    private static final String MASTER_PATH = "/master";
    private ZkClient zkClient;
    private RunningData serverData;
    private RunningData masterData;
    private IZkDataListener dataListener;
    private IZkChildListener childListener;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public WorkServer(RunningData rd) {
        this.serverData = rd;
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
//                takeMaster();
                if (masterData != null && masterData.getName().equals(serverData.getName())) {
                    takeMaster();
                } else {
                    executorService.schedule(() -> takeMaster(), 5, TimeUnit.SECONDS);
                }
            }
        };
        this.childListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + " 's child changed, currentChilds: " + currentChilds);
                if (currentChilds != null && currentChilds.size() > 0) {
                    for (String node : currentChilds) {
                        String path = parentPath + "/" + node;
                        System.out.println(path);
                    }
                }
            }
        };
    }

    public void start() throws Exception {
        if (running) {
            throw new Exception("server has startup...");
        }
        running = true;
        zkClient.subscribeDataChanges(MASTER_PATH, dataListener);
        zkClient.subscribeChildChanges(MASTER_PATH, childListener);
        takeMaster();
    }

    public void stop() throws Exception {
        if (!running) {
            throw new Exception("Server has stopped");
        }
        running = false;
        zkClient.unsubscribeDataChanges(MASTER_PATH, dataListener);
        zkClient.unsubscribeChildChanges(MASTER_PATH, childListener);
        releaseMaster();
    }

    private void releaseMaster() {
        if (checkMaster()) {
            zkClient.delete(MASTER_PATH);
        }
    }

    private void takeMaster() {
        if (!running) {
            return;
        }
        try {
            zkClient.createEphemeral(MASTER_PATH, serverData);
            masterData = serverData;
            System.out.println(serverData.getName() + " is master");
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    if (checkMaster()) {
                        System.out.println("释放选举");
                        releaseMaster();
                    }
                }
            }, 5, TimeUnit.SECONDS);
        } catch (ZkNodeExistsException e) {
            System.out.println("master node exist");
            RunningData runningData = zkClient.readData(MASTER_PATH);
            if (runningData == null) {
                takeMaster();
            } else {
                masterData = runningData;
            }
        }
    }

    private boolean checkMaster() {
        try {
            RunningData runningData = zkClient.readData(MASTER_PATH);
            masterData = runningData;
            if (masterData.getName().equals(serverData.getName())) {
                return true;
            }
        } catch (ZkNoNodeException e) {
            return false;
        } catch (ZkInterruptedException e) {
            checkMaster();
        } catch (ZkException e) {
            return false;
        }
        return false;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }
}
