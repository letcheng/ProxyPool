/**
 *
 */
package com.ruyuapp.util;

import com.ruyuapp.proxy.HttpProxy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 将添加Proxy记录进行持久化保存
 *
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 23:45
 */
public class ProxyPersistUtils {

    private final static Logger logger = LoggerFactory.getLogger(ProxyPersistUtils.class);

    private File path; // 保存proxy的路径

    public ProxyPersistUtils(){
        this.path = getFile();
    }

    public Map<String,HttpProxy> read() {
        Map<String,HttpProxy> map = new ConcurrentHashMap<String, HttpProxy>();
        String content = null;
        try {
            content = FileUtils.readFileToString(this.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(content != null && content.length() > 0){
            String lines[] = content.split("\n");
            for(String line : lines){
                String tmps[] = line.split(":");
                Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(tmps[0],Integer.parseInt(tmps[1])));
                HttpProxy httpProxy = new HttpProxy(proxy,
                        Integer.parseInt(tmps[2]),
                        Integer.parseInt(tmps[3]),
                        Integer.parseInt(tmps[4]));
                map.put(httpProxy.getKey(),httpProxy);
            }
        }
        return map;
    }

    public void save(Map<String, HttpProxy> totalQueue){
        if (totalQueue.size() == 0) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String,HttpProxy> entry : totalQueue.entrySet()){
            InetSocketAddress address = (InetSocketAddress) entry.getValue().getProxy().address();
            stringBuilder.append(address.getAddress().getHostAddress()).append(":")
                    .append(address.getPort()).append(":")
                    .append(entry.getValue().getBorrowNum()).append(":")
                    .append(entry.getValue().getFailedNum()).append(":")
                    .append(entry.getValue().getReuseTimeInterval()).append("\n");
        }
        try {
            FileUtils.write(this.path,stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(){
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "proxy.tmp");
        if(!file.exists()) {
            try {
                if(!file.createNewFile()){
                    throw new IOException(file+"create error");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
