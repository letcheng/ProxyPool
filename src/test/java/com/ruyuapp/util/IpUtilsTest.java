/**
 *
 */
package com.ruyuapp.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
