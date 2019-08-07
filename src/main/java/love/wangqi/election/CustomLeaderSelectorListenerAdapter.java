package love.wangqi.election;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 15:45
 */
public class CustomLeaderSelectorListenerAdapter extends LeaderSelectorListenerAdapter implements Closeable {
    private String name;
    private LeaderSelector leaderSelector;

    public CustomLeaderSelectorListenerAdapter(CuratorFramework client, String path, String name) {
        this.name = name;
        this.leaderSelector = new LeaderSelector(client, path, this);
    }

    public void start() {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        final int waitSeconds = 2;

        System.out.println(name + " 成为当前leader");

        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            System.out.println(name + "已被中断");
        } finally {
            System.out.println(name + "放弃领导权\n");
        }
    }
}
