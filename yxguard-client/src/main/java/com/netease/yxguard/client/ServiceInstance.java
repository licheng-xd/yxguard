package com.netease.yxguard.client;

import com.google.common.base.Preconditions;
import com.netease.yxguard.utils.IPUtil;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

/**
 * 服务实例,只能通过builder方式创建.
 *
 * Created by lc on 16/6/5.
 */
public class ServiceInstance implements Cloneable, Serializable {

    private static final long serialVersionUID = 9129768984122577450L;

    /**
     * 服务名称,同样服务名称的服务实例可以有多个.
     */
    private String name;

    /**
     * 服务id(唯一),默认用uuid
     */
    private String id;

    private String hostname;

    private String address;

    private int port;

    private long regtime;

    private String url;

    private int version;

    private ServiceType serviceType;

    /**
     * 自定义内容,建议自扩展json
     */
    private String payload;

    private ServiceInstance(ServiceInstanceBuilder builder) {
        builder.name = Preconditions.checkNotNull(builder.name, "service name cannot be null");
        builder.id = Preconditions.checkNotNull(builder.id, "service id cannot be null");
        this.name = builder.name;
        this.id = builder.id;
        this.address = builder.address;
        this.port = builder.port;
        this.regtime = builder.regtime;
        this.url = builder.url;
        this.version = builder.version;
        this.payload = builder.payload;
        this.serviceType = builder.serviceType;
        this.hostname = builder.hostname;
    }

    public static ServiceInstanceBuilder builder() throws Exception {
        // 默认用第一个本地ip地址
        String address = IPUtil.getLocalIP();
        // 默认用uuid作为service的唯一id
        String id = UUID.randomUUID().toString();
        String hostname = InetAddress.getLocalHost().getHostName();
        return new ServiceInstanceBuilder().id(id).address(address).hostname(hostname)
            .regtime(System.currentTimeMillis()).serviceType(ServiceType.DYNAMIC);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return port;
    }

    public long getRegtime() {
        return regtime;
    }

    public String getUrl() {
        return url;
    }

    public String getPayload() {
        return payload;
    }

    public int getVersion() {
        return version;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
            "name='" + name + '\'' +
            ", id='" + id + '\'' +
            ", hostname='" + hostname + '\'' +
            ", address='" + address + '\'' +
            ", port=" + port +
            ", regtime=" + regtime +
            ", url='" + url + '\'' +
            ", version='" + version + '\'' +
            ", serviceType=" + serviceType +
            ", payload=" + payload +
            '}';
    }

    /**
     * builder for service instance
     */
    public static class ServiceInstanceBuilder {

        private String name;
        private String id;
        private String address;
        private int port;
        private long regtime;
        private String url;
        private int version;
        private ServiceType serviceType;
        private String payload;
        private String hostname;


        private ServiceInstanceBuilder() {}

        public ServiceInstance build() {
            return new ServiceInstance(this);
        }

        public ServiceInstanceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ServiceInstanceBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ServiceInstanceBuilder address(String address) {
            this.address = address;
            return this;
        }

        public ServiceInstanceBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public ServiceInstanceBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServiceInstanceBuilder regtime(long regtime) {
            this.regtime = regtime;
            return this;
        }

        public ServiceInstanceBuilder url(String url) {
            this.url = url;
            return this;
        }

        public ServiceInstanceBuilder version(int version) {
            this.version = version;
            return this;
        }

        public ServiceInstanceBuilder serviceType(ServiceType serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public ServiceInstanceBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }
    }

}
