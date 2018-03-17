import top.yelinsheng.torhop.proxy.GateWay;
import top.yelinsheng.torhop.proxy.Leader;
import top.yelinsheng.torhop.proxy.Slave;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;

public class MainTester {
    public static void main(String[] args) {
        final Address slave0ProxyAddress = new Address("127.0.0.1", 9000);
        final Address slave1ProxyAddress = new Address("127.0.0.1", 9001);
        final Address slave2ProxyAddress = new Address("127.0.0.1", 9002);
        final Address slave3ProxyAddress = new Address("127.0.0.1", 9003);
        final Address slave4ProxyAddress = new Address("127.0.0.1", 9004);

        final Address gateWayAddress = new Address("127.0.0.1", 8000);

        final Address leaderAddress = new Address("127.0.0.1", 7000);

        Router slave0Router = new DefaultRouter(slave0ProxyAddress, null, leaderAddress, "slave");
        Router slave1Router = new DefaultRouter(slave1ProxyAddress, null, leaderAddress, "slave");
        Router slave2Router = new DefaultRouter(slave2ProxyAddress, null, leaderAddress, "slave");
        Router slave3Router = new DefaultRouter(slave3ProxyAddress, null, leaderAddress, "slave");
        Router leaderRouter = new DefaultRouter(slave4ProxyAddress, null, leaderAddress, "slave");
        Router gateWayRouter = new DefaultRouter(gateWayAddress, null, leaderAddress, "gateWay");

        Slave slave0 = new Slave(slave0Router);
        Slave slave1 = new Slave(slave1Router);
        Slave slave2 = new Slave(slave2Router);
        Slave slave3 = new Slave(slave3Router);
        GateWay gateWay = new GateWay(gateWayRouter);
        Leader leader = new Leader(leaderRouter);

        leader.startLeaderService();
        leader.startProxyService();
        slave0.startProxyService();
        slave1.startProxyService();
        slave2.startProxyService();
        slave3.startProxyService();
        gateWay.startProxyService();
    }
}
