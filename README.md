# ProxyPool

[![Build Status](https://travis-ci.org/letcheng/ProxyPool.svg?branch=master)](https://travis-ci.org/letcheng/ProxyPool)
[![Release](https://jitpack.io/v/letcheng/ProxyPool.svg)](https://jitpack.io/#letcheng/ProxyPool)

针对反爬虫问题的自动代理池组件

### 特色

* 支持 Proxy 自动持久化，一次加入，永久可使用
* 自动将不可用的 Proxy 移出代理池
* 线程安全，支持多线程同时使用线程池
* 优先选择响应速度快的 Proxy
* 出现 403 等状态码时，自动降低该 Proxy 的访问频率

### 使用

1. 添加 Maven 库
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```
<dependency>
    <groupId>com.github.letcheng</groupId>
    <artifactId>ProxyPool</artifactId>
    <version>x.x</version>
</dependency>
```

2. 采取 add(init) -> borrow -> reback 的方式进行使用

```java
ProxyPool proxyPool = new ProxyPool();

proxyPool.add("203.171.230.230", 80);
proxyPool.add("121.9.221.188", 80);

HttpProxy httpProxy = proxyPool.borrow(); // 从 ProxyPool 中获取一个Proxy

proxyPool.reback(httpProxy, HttpStatus.SC_OK); // 使用完成之后，归还 Proxy,并将请求结果的 http 状态码一起传入

proxyPool.allProxyStatus();  // 可以获取 ProxyPool 中所有 Proxy 的当前状态
```



