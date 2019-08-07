package love.wangqi.leader_election;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 08:43
 */
public class CustomLeaderSelectorListenerAdapter extends LeaderSelectorListenerAdapter implements Closeable {

    private String name;
    private LeaderSelector leaderSelector;
    private AtomicInteger leaderCount = new AtomicInteger();

    public CustomLeaderSelectorListenerAdapter(CuratorFramework client, String path, String name) {
        this.name = name;
        this.leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
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
        System.out.println(name + "成为当前leader");
        System.out.println(name + " 之前成为leader的次数：" + leaderCount.getAndIncrement() + " 次");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (InterruptedException e) {
            System.out.println(name + "已被中断");
        } finally {
            System.out.println(name + "放弃领导权\n");
        }
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        System.out.println("当前状态为：" + newState.toString());
        super.stateChanged(client, newState);
    }
}
