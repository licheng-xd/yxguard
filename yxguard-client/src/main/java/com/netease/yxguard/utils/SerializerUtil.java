package com.netease.yxguard.utils;

import com.netease.yxguard.client.ServiceInstance;

import java.io.*;

/**
 * 服务实例序列化工具
 *
 * Created by lc on 16/6/17.
 */
public class SerializerUtil {

    public static  byte[] serialize(ServiceInstance instance) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(instance);
        outputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static  ServiceInstance deserialize(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        ServiceInstance instance = (ServiceInstance) inputStream.readObject();
        inputStream.close();
        return instance;
    }

}
