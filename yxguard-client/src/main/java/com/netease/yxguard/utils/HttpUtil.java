package com.netease.yxguard.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.yxguard.config.GuardConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 简单http工具
 *
 * Created by lc on 16/6/21.
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final CloseableHttpClient client = HttpClients.createDefault();

    public static String get(String url) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "utf-8");
            } else {
                logger.error("http get failed, url: {} ,retcode: {}", url, sl.getStatusCode());
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
          logger.error(e.getMessage(), e);
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    public static GuardConfig getConfig(String url) {
        GuardConfig config = new GuardConfig();
        String resp = HttpUtil.get(url);
        JSONObject respJ = JSON.parseObject(resp);
        if (respJ.getInteger("code") == 200) {
            config = GuardConfig.fromJSON(respJ.getString("obj"));
        }
        return config;
    }

    public static void main(String[] args) {
        System.out.println(getConfig("http://127.0.0.1:8000/yxguard/client/config"));
    }
}
