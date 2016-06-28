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




