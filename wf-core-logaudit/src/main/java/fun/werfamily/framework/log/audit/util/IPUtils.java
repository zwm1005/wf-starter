package fun.werfamily.framework.log.audit.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public final class IPUtils {
    private static final String DEFAULT_IP = "0.0.0.0";

    private IPUtils() {
    }

    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || !netInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress address : netInterface.getInterfaceAddresses()) {
                    InetAddress ip = address.getAddress();
                    if (ip != null && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() && ip.isSiteLocalAddress()) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {

        }

        return DEFAULT_IP;
    }
}