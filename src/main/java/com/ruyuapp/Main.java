/**
 *
 */
package com.ruyuapp;

import com.ruyuapp.proxy.HttpProxy;
import com.ruyuapp.proxy.ProxyPool;
import com.ruyuapp.util.HttpStatus;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月28日 15:32
 */
public class Main {

    /**
     * 为了阻塞主线程，不在junit中测试
     *
     * @param args
     */
    public static void main(String args[]) {

        ProxyPool proxyPool = new ProxyPool();

        proxyPool.add("203.171.230.230", 80);
        proxyPool.add("121.9.221.188", 80);

        HttpProxy httpProxy = proxyPool.borrow(); // 从 ProxyPool 中获取一个Proxy

        URL url = null;
        try {
            url = new URL("http://www.ruyuapp.com");
            HttpURLConnection uc = (HttpURLConnection)url.openConnection(httpProxy.getProxy());
            System.out.println(uc.getResponseCode());
            uc.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        proxyPool.reback(httpProxy, HttpStatus.SC_OK); // 使用完成之后，归还 Proxy,并将请求结果的 http 状态码一起传入

        proxyPool.allProxyStatus();  // 可以获取 ProxyPool 中所有 Proxy 的当前状态


    }

}
