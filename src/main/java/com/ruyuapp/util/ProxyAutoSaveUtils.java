/**
 *
 */
package com.ruyuapp.util;

import com.ruyuapp.proxy.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 *
 * 将添加Proxy记录进行持久化保存
 *
 * @author <a href="mailto:letcheng@ruyuapp.com">letcheng</a>
 * @version create at 2016年3月27日 23:45
 */
public class ProxyAutoSaveUtils {

    private final static Logger logger = LoggerFactory.getLogger(ProxyAutoSaveUtils.class);

    private File path; // 保存proxy的路径

    public ProxyAutoSaveUtils(){
        this.path = getFile();
    }

    public Map<String,HttpProxy> read() {
        Object obj = null;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(this.path));
            obj = is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (Map<String, HttpProxy>) obj;
    }

    public void save(Map<String, HttpProxy> totalQueue){
        if (totalQueue.size() == 0) {
            return;
        }
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(this.path));
            os.writeObject(totalQueue);
            os.close();
            logger.info("save proxy");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(){
        File file = new File(System.getProperty("java.io.tmpdir") + File.pathSeparator + "proxy.tmp");
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
