import com.netease.yxguard.client.GuardClient;
import com.netease.yxguard.impl.GuardClientBuilder;
import com.netease.yxguard.client.ServiceCache;
import com.netease.yxguard.client.ServiceCacheListener;
import com.netease.yxguard.config.GuardConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.ZKPaths;

/**
 * Created by lc on 16/6/22.
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {
        GuardConfig config = new GuardConfig();
        config.setBasePath("/yxguard");
        config.setZkAddr("127.0.0.1:2181");
        GuardClient client = GuardClientBuilder.builder().config(config).watchInstances(true).build();

        client.start();
        // 监听服务列表
        PathChildrenCache rootCache = new PathChildrenCache(client.getClient(), config.getBasePath(), true);
        rootCache.getListenable().addListener(
            new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework,
                    PathChildrenCacheEvent event)
                    throws Exception {
                    // 刷新服务列表
                    switch (event.getType()) {
                        case CHILD_ADDED:
                        case CHILD_UPDATED: {
                            System.out.println("child add or update");
                            break;
                        }
                        case CHILD_REMOVED: {
                            System.out.println("child remove");
                            break;
                        }
                    }
                }
            });

        // 初始化服务列表
        rootCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        for (ChildData data : rootCache.getCurrentData()) {
            String name = ZKPaths.getNodeFromPath(data.getPath());
            System.out.println(name);
            final ServiceCache cache = client.serviceCacheBuilder().name(name)
                .build();
            cache.addListener(new ServiceCacheListener() {
                @Override
                public void cacheChanged(PathChildrenCacheEvent event) {
                    // 服务节点有变化
                    System.out.println("service cache changed, name:" + cache.getName());
                }

                @Override
                public void stateChanged(CuratorFramework curatorFramework,
                    ConnectionState connectionState) {

                }
            });
            try {
                cache.start();
                System.out.println(cache.getInstances());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
