# torhop
此项目模拟了代理服务器的自组织与转发

## 编译
./make.sh

## 启动服务
### 启动leader服务
./start.sh -role leader -leaderaddress ip:port -proxyaddress ip:port
### 启动slave服务
./start.sh -role slave -leaderaddress ip:port -proxyaddress ip:port
###启动gateway服务
./start.sh -role gateway -leaderaddress ip:port -proxyaddress ip:port

##注意事项
* 请使用公网ip