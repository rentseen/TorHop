# torhop
此项目模拟了代理服务器的自组织与转发

## 简介
此项目的灵感来自于美国电影里黑客们追踪坏人地址的场景，坏人为了防止被追踪，会将自己的电话、网络数据等通过很多个代理转发。黑客们的GUI上就会显示一条条网络数据的线路，但无法追踪源头。  
在现实中也有相关的技术，即最初由美国海军研究实验室发起并公开的[Tor](https://www.torproject.org/)。在Tor中，客户端连接到目标服务器会经过几个随机的公共代理服务器，当用户众多时，客户端的流量会被隐藏在众多数据流中。  
此项目主要就是模拟了多个代理服务器集合的动态管理以及转发管理。  
其原理如下图所示：


        ┌────────┐                 ┌────────┐              ┌────────┐                ┌────────┐   
        │        │   nexthop       │        │    nexthop   │        │   nexthop      │        │   
        │ Slave  ◀─────────────────┤ Slave  ◀──────────────┤ Slave  ◀────────────────┤ Slave  │◀─┐
        │        │                 │        │              │        │                │        │  │
        └────┬───┘                 └────┬───┘              └────┬───┘                └───┬────┘  │
             │                          │                       │                        │       │
     register│                  register│               register│                register│       │
             │                          │                       │                        │       │
        ┌────▼──────────────────────────▼───────────────────────▼────────────────────────▼─────┐ │
        │                                                                                      │ │
        │                                        Leader                                        │ │
        │                                                                                      │ │
        └──────────────────────────────────────────▲───────────────────────────────────────────┘ │
                                                   │                                             │
                                           register│                                             │
                                                   │                                             │
                                              ┌────┴───┐                                         │
                                              │        │                nexthop                  │
                                              │GateWay │─────────────────────────────────────────┘
                                              │        │                                          
                                              └────▲───┘                                          
                                                   │                                              
                                            connect│                                              
                                             ┌─────┴────┐                                         
                                             │  Client  │                                         
                                             └──────────┘                                                                                 


## 编译
```
./make.sh
```
## 启动服务
### 启动leader服务
```
./start.sh -role leader -leaderaddress ip:port -proxyaddress ip:port
```
### 启动slave服务
```
./start.sh -role slave -leaderaddress ip:port -proxyaddress ip:port
```
### 启动gateway服务
```
./start.sh -role gateway -leaderaddress ip:port -proxyaddress ip:port
```

## 客户端
可在chrome浏览器上安装SwitchyOmega插件，将gateway配置为代理

## 注意事项
* 如果服务不在本地，请使用公网ip