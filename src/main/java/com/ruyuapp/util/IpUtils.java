/**
 *
 */
package com.ruyuapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 10:47
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private static class IpUtilsHolder {
        private static final InetAddress localAddr = new IpUtils().init();
    }

    private IpUtils(){

    }

    public static InetAddress getLocalAddr(){
        return IpUtilsHolder.localAddr;
    }

    private InetAddress init(){
        try {
            return InetAddress.getLocalHost(); //直接获取IP地址，适应于Windows机器
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Enumeration<NetworkInterface> enumeration = null; //遍历所有的网络接口获取
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e){
            e.printStackTrace();
        }
        while (enumeration != null && enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            Enumeration<InetAddress> addr = networkInterface.getInetAddresses();
            while (addr.hasMoreElements()) {
                InetAddress localAddr = addr.nextElement();
                Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
                if (localAddr.getHostAddress() != null && IPV4_PATTERN.matcher(localAddr.getHostAddress()).matches()) {
                    return localAddr;
                }
            }
        }
        return null;
    }
}
