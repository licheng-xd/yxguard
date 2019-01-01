package com.netease.yxguard.utils;

import com.google.common.collect.Lists;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lc on 16/6/4.
 */
public class IPUtil {

    private static final AtomicReference<LocalIPFilter> localIpFilter = new AtomicReference<LocalIPFilter>(
        new LocalIPFilter() {
            @Override
            public boolean use(NetworkInterface nif, InetAddress adr) throws SocketException {
                return (adr != null) && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress());
            }
        }
    );

    public static String getLocalIP() throws SocketException {
        return getAllLocalIPs().iterator().next().getHostAddress();
    }

    public static Collection<InetAddress> getAllLocalIPs() throws SocketException {
        List<InetAddress> listAdr = Lists.newArrayList();
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        if (nifs == null) return listAdr;

        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> adrs = nif.getInetAddresses();
            while (adrs.hasMoreElements()) {
                InetAddress adr = adrs.nextElement();
                if (localIpFilter.get().use(nif, adr)) {
                    listAdr.add(adr);
                }
            }
        }
        return listAdr;
    }
}
