package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class Slave extends Proxy {

    public Slave(Router router) {
        super(router);
    }

    public static void main(String[] args) {
        final Address slave0ProxyAddress = new Address("127.0.0.1", 9000);
        final Address slave1ProxyAddress = new Address("127.0.0.1", 9001);
        final Address slave2ProxyAddress = new Address("127.0.0.1", 9002);
        final Address slave3ProxyAddress = new Address("127.0.0.1", 9003);
        final Address leaderAddress = new Address("127.0.0.1", 7000);
        Router slave0Router = new DefaultRouter(slave0ProxyAddress, null, leaderAddress, "slave");
        Router slave1Router = new DefaultRouter(slave1ProxyAddress, null, leaderAddress, "slave");
        Router slave2Router = new DefaultRouter(slave2ProxyAddress, null, leaderAddress, "slave");
        Router slave3Router = new DefaultRouter(slave3ProxyAddress, null, leaderAddress, "slave");
        Slave slave0 = new Slave(slave0Router);
        Slave slave1 = new Slave(slave1Router);
        Slave slave2 = new Slave(slave2Router);
        Slave slave3 = new Slave(slave3Router);
        slave0.startProxyService();
        slave1.startProxyService();
        slave2.startProxyService();
        slave3.startProxyService();
    }

}
