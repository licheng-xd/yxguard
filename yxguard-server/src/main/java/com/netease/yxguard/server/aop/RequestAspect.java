package com.netease.yxguard.server.aop;

import com.netease.yxguard.server.log.LogUtil;
import com.netease.yxguard.server.meta.RespBody;
import com.netease.yxguard.utils.RegexUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created by lc on 01/07/15.
 */

@Aspect
@Component
public class RequestAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestAspect.class);

    @Around("within(@org.springframework.stereotype.Controller *)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        LogUtil.init();

        MethodSignature methodSignature = (MethodSignature) point
            .getSignature();
        Method reqMethod = methodSignature.getMethod();
        reqMethod.getParameterAnnotations();
        String methodname = reqMethod.getName();
        LogUtil.addProp("method", methodname);
        String uri = request.getRequestURI();
        LogUtil.addProp("uri", uri);
        long starttime = System.currentTimeMillis();
        LogUtil.setStarttime(starttime);
        try {
            String host = getRequestIp(request);
            LogUtil.addProp("host", host);
            Object ret = point.proceed();
            if (ret instanceof RespBody)
                LogUtil.setResp(ret);
            return ret;
        } finally {
            long endtime = System.currentTimeMillis();
            LogUtil.setSpendtime(endtime - starttime);
            LogUtil.info();
        }
    }

    public static String getRequestIp(HttpServletRequest request) {
        try {
            String realIp = null;
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isEmpty()) {
                String[] ips = xff.split(",");
                for (String ip: ips) {
                    if (!RegexUtil.isInternalIp(ip)) {
                        realIp = ip;
                        break;
                    }
                }
            }
            if (realIp == null) {
                realIp = request.getRemoteAddr();
            }
            realIp = realIp.trim();
            return realIp;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }
}
