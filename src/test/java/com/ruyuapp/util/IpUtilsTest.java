/**
 *
 */
package com.ruyuapp.util;

import com.ruyuapp.proxy.HttpProxy;
import com.ruyuapp.proxy.ProxyPool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 16:37
 */
public class IpUtilsTest {

    private final static Logger logger = LoggerFactory.getLogger(IpUtilsTest.class);

    @Test
    public void testGetLocalIp(){
        logger.info("Local ip >>> " + IpUtils.getLocalAddr().toString());
    }

}
