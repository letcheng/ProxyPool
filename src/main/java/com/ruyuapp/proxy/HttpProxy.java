package com.ruyuapp.proxy;

import com.ruyuapp.util.HttpStatus;
import com.ruyuapp.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 10:47
 */
public class HttpProxy implements Delayed, Serializable {

	private final static long serialVersionUID = 228939737383625551L;
    private final static Logger logger = LoggerFactory.getLogger(HttpProxy.class);

    public final static int DEFAULT_REUSE_TIME_INTERVAL = 1500;// ms，从一次请求结束到再次可以请求的默认时间间隔
    public final static int FAIL_REVIVE_TIME_INTERVAL = 2 * 60 * 60 * 1000; //ms,请求失败，重试的时间间隔

    private Proxy proxy;
    private InetAddress localAddr;

	private int reuseTimeInterval = DEFAULT_REUSE_TIME_INTERVAL;
	private Long canReuseTime = 0L;  // 当前Proxy可重用的时间,纳秒

	private int failedNum = 0;
	private int borrowNum = 0;

    private Map<HttpStatus,Integer> countErrorStatus = new HashMap<HttpStatus, Integer>();

	public HttpProxy(Proxy proxy) {
        this.localAddr = IpUtils.getLocalAddr(); // 获取当前机器的ip地址
        if(localAddr == null){
            logger.error("cannot get local IP!");
            System.exit(0);
        }
        this.proxy = proxy;
		this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);
	}

    /**
     * 检查本地机器和Proxy之间的连通性
     * @return
     */
    public boolean check() {
        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();
            socket.bind(new InetSocketAddress(localAddr, 0));
            socket.connect(proxy.address(), 3000);
            isReachable = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isReachable;
    }

	public void success() {
        this.failedNum = 0; //将 failNum 清零
        countErrorStatus.clear();
	}

    /**
     * 代理失败，记录相应的错误状态码
     *
     * @param httpStatus
     */
    public void fail(HttpStatus httpStatus) {
        if(countErrorStatus.containsKey(httpStatus)){
            countErrorStatus.put(httpStatus,countErrorStatus.get(httpStatus)+1);
        }else{
            countErrorStatus.put(httpStatus,1);
        }
        this.failedNum++;
    }

    /**
     * 输出错误请求的日志
     * @return
     */
    public String countErrorStatus() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<HttpStatus,Integer> entry : countErrorStatus.entrySet()) {
            stringBuilder.append(entry.getKey().name()).append("_").append(entry.getKey().getCode()).append("->").append(entry.getValue());
        }
        return stringBuilder.toString();
    }

    /**
     * 对请求进行计数
     * @param increment
     */
	public void borrow() {
		this.borrowNum++;
	}

    public int getFailedNum() {
        return failedNum;
    }

    public int getBorrowNum() {
        return borrowNum;
    }

    public void setReuseTimeInterval(int reuseTimeInterval) {
        this.reuseTimeInterval = reuseTimeInterval;
    }

    public int getReuseTimeInterval(){
        return this.reuseTimeInterval;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public int compareTo(Delayed o) {
        HttpProxy that = (HttpProxy) o;
        return canReuseTime > that.canReuseTime ? 1 : (canReuseTime < that.canReuseTime ? -1 : 0);
    }

    public long getDelay(TimeUnit unit) {
        return unit.convert(canReuseTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

}
