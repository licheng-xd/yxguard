package com.netease.yxguard.server.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * http请求日志记录
 *
 * Created by lc on 16/1/25.
 */
public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    private static ThreadLocal<LogBean> logBean = new ThreadLocal<LogBean>();

    public static void init() {
        get().setProps(new HashMap<String, Object>());
    }

    public static void setResp(Object resp) {
        if (resp != null) {
            get().setResp(resp.toString());
        }
    }

    public static void setStarttime(long starttime) {
        get().setStarttime(starttime);
    }

    public static void setSpendtime(long spendtime) {
        get().setSpendtime(spendtime);
    }

    public static void addProp(String key, Object value) {
        get().addProp(key, value);
    }

    public static void setProp(Map<String, Object> prop) {
        get().setProps(prop);
    }

    private static LogBean get() {
        LogBean lb = logBean.get();
        if (lb == null) {
            lb = LogBean.getLog();
            logBean.set(lb);
        }
        return lb;
    }

    public static void info() {
        logger.info(get().toString());
    }

    @JSONType(orders = {"sid", "cid", "retcode", "spendtime", "props", "starttime"})
    public static class LogBean {
        private String resp;

        private long starttime;

        private long spendtime;

        private Map<String, Object> props = new HashMap<String, Object>();

        private LogBean() {
            this.starttime = System.currentTimeMillis();
        }

        public static LogBean getLog() {
            return new LogBean();
        }

        public void addProp(String key, Object value) {
            props.put(key, value);
        }

        public Map<String, Object> getProps() {
            return props;
        }

        public void setProps(Map<String, Object> props) {
            this.props = props;
        }

        public String getResp() {
            return resp;
        }

        public void setResp(String resp) {
            this.resp = resp;
        }

        public long getSpendtime() {
            return spendtime;
        }

        public void setSpendtime(long spendtime) {
            this.spendtime = spendtime;
        }

        public Long getStarttime() {
            return starttime;
        }

        public void setStarttime(Long starttime) {
            this.starttime = starttime;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }

    }
}
