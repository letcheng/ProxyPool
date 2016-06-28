package com.ruyuapp.proxy;

import com.ruyuapp.util.HttpStatus;
import com.ruyuapp.util.ProxyAutoSaveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 10:47
 */
public class ProxyPool {

    private Logger logger = LoggerFactory.getLogger(ProxyPool.class);

    private BlockingQueue<HttpProxy> idleQueue = new DelayQueue<HttpProxy>(); // 存储空闲的Proxy
    private Map<String, HttpProxy> totalQueue = new ConcurrentHashMap<String, HttpProxy>(); // 存储所有的Proxy

    public ProxyPool() {
        final ProxyAutoSaveUtils proxyAutoSave = new ProxyAutoSaveUtils();
        // 读取上次的Proxy记录
        this.totalQueue = proxyAutoSave.read();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            public void run() {
                proxyAutoSave.save(totalQueue);
            }
        }, 0, 5, TimeUnit.MINUTES); //10分钟执行保存Proxy的操作
    }

    /**
     * 添加Proxy
     *
     * @param httpProxies
     */
    private void add(HttpProxy... httpProxies) {
        for (HttpProxy httpProxy : httpProxies) {
            if (totalQueue.containsKey(httpProxy.getProxy().toString())) {
                continue;
            }
            if (httpProxy.check()) {
                httpProxy.success();
                idleQueue.add(httpProxy);
                totalQueue.put(httpProxy.toString(), httpProxy);
            }
        }
    }

    /**
     * 得到Proxy
     *
     * @return
     */
    public HttpProxy borrow() {
        HttpProxy httpProxy = null;
        try {
            Long time = System.currentTimeMillis();
            httpProxy = idleQueue.take();
            double costTime = (System.currentTimeMillis() - time) / 1000.0;
            logger.info("get proxy time >>>> " + costTime);

            HttpProxy p = totalQueue.get(httpProxy.getProxy().toString());
            p.borrow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (httpProxy == null) {
            throw new NoSuchElementException();
        }
        return httpProxy;
    }

    /**
     * 反馈 Proxy
     *
     * @param httpProxy
     * @param httpStatus
     */
    public void reback(HttpProxy httpProxy, HttpStatus httpStatus) {
        switch (httpStatus) {
            case SC_OK:
                httpProxy.success();
                httpProxy.setReuseTimeInterval(HttpProxy.DEFAULT_REUSE_TIME_INTERVAL);
                break;
            case SC_FORBIDDEN:
                httpProxy.fail(httpStatus);
                httpProxy.setReuseTimeInterval(HttpProxy.DEFAULT_REUSE_TIME_INTERVAL * httpProxy.getFailedNum()); // 被网站禁止，调节更长时间的访问频率
                logger.info(httpProxy.getProxy() + " >>>> reuseTimeInterval is >>>> " + TimeUnit.SECONDS.convert(httpProxy.getReuseTimeInterval(),TimeUnit.MILLISECONDS));
                break;
            default:
                httpProxy.fail(httpStatus);
                break;
        }
        if (httpProxy.getFailedNum() > 20) { // 失败超过 20 次，移除代理池队列
            httpProxy.setReuseTimeInterval(HttpProxy.FAIL_REVIVE_TIME_INTERVAL);
            logger.error("remove proxy >>>> " + httpProxy.getProxy() + ">>>>" + httpProxy.countErrorStatus() + " >>>> remain proxy >>>> " + idleQueue.size());
            return;
        }
        if (httpProxy.getFailedNum() > 0 && httpProxy.getFailedNum() % 5 == 0) { //失败超过 5次，10次，15次，检查本机与Proxy的连通性
            if (!httpProxy.check()) {
                httpProxy.setReuseTimeInterval(HttpProxy.FAIL_REVIVE_TIME_INTERVAL);
                logger.error("remove proxy >>>> " + httpProxy.getProxy() + ">>>>" + httpProxy.countErrorStatus() + " >>>> remain proxy >>>> " + idleQueue.size());
                return;
            }
        }
        try {
            idleQueue.put(httpProxy);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String allProxyStatus() {
        String re = "all proxy info >>>> \n";
        for (Entry<String, HttpProxy> entry : totalQueue.entrySet()) {
            re += entry.getValue().toString() + "\n";
        }
        return re;
    }

    /**
     *  获取当前空闲的Proxy
     *
     * @return
     */
    public int getIdleNum() {
        return idleQueue.size();
    }

}
