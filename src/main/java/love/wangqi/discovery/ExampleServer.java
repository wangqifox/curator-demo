package love.wangqi.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 16:40
 */
public class ExampleServer implements Closeable {
    private final ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private final ServiceInstance<InstanceDetails> thisInstance;

    public ExampleServer(CuratorFramework client, String path, String serviceName, String description) throws Exception {
        UriSpec uriSpec = new UriSpec("{scheme}://foo.com:{port}");

        thisInstance = ServiceInstance.<InstanceDetails>builder()
                .name(serviceName)
                .payload(new InstanceDetails(description))
                .port((int)(65536 * Math.random()))
                .uriSpec(uriSpec)
                .build();

        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<>(InstanceDetails.class);

        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(client)
                .basePath(path)
                .serializer(serializer)
                .thisInstance(thisInstance)
                .build();
    }

    public ServiceInstance<InstanceDetails> getThisInstance() {
        return thisInstance;
    }

    public void start() throws Exception {
        serviceDiscovery.start();
    }

    @Override
    public void close() throws IOException {

    }
}
