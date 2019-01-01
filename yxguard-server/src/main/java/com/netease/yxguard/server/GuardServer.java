package com.netease.yxguard.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * yxguard-server入口
 *
 * Created by lc on 16/6/22.
 */
public class GuardServer {
    private static final Logger logger = LoggerFactory.getLogger(GuardServer.class);

    private static final String WEB_XML = "webapp/WEB-INF/web.xml";
    private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "com.netease.yxguard.server.IDE";
    private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";

//    private Server server;

    /*public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        int port = 8000;
        try {
            prop.load(ClassLoader.getSystemResourceAsStream("yxguard-server.properties"));
            String sport = prop.getProperty("server.port");
            if (sport != null) {
                port = Integer.parseInt(sport);
            }
        } catch (IOException e) {
            logger.error("get properties error", e);
        }
        GuardServer jServer = new GuardServer();
        jServer.start(port);
        logger.info("Server started at port {}", port);
        jServer.join();
    }

    public void start(int port) throws Exception {
        server = new Server(port);
        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);
        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private HandlerCollection createHandlers() {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        if (isRunningInShadedJar()) {
            context.setWar(getShadedWarUrl());
        } else {
            context.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
        }

        List<Handler> handlers = Lists.newArrayList();
        handlers.add(context);

        HandlerList contexts = new HandlerList();
        contexts.setHandlers(handlers.toArray(new Handler[handlers.size()]));

        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[] { context, });
        return collection;
    }

    private boolean isRunningInShadedJar() {
        try {
            Class.forName(CLASS_ONLY_AVAILABLE_IN_IDE);
            return false;
        } catch (ClassNotFoundException anExc) {
            return true;
        }
    }

    private URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader()
            .getResource(aResource);
    }

    private String getShadedWarUrl() {
        String urlStr = getResource(WEB_XML).toString();
        return urlStr.substring(0, urlStr.length() - 15);
    }*/
}
