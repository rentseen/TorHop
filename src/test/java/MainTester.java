import top.yelinsheng.torhop.proxy.GateWay;
import top.yelinsheng.torhop.proxy.Leader;
import top.yelinsheng.torhop.proxy.Slave;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;

public class MainTester {
    public static void main(String[] args) {
        final Address slave0ProxyAddress = new Address("127.0.0.1", 9010);
        final Address slave1ProxyAddress = new Address("127.0.0.1", 9011);


        final Address leaderAddress = new Address("127.0.0.1", 7000);

        Router slave0Router = new DefaultRouter(slave0ProxyAddress, null, leaderAddress, "slave");
        Router slave1Router = new DefaultRouter(slave1ProxyAddress, null, leaderAddress, "slave");

        Slave slave0 = new Slave(slave0Router);
        Slave slave1 = new Slave(slave1Router);


        slave0.startProxyService();
        slave1.startProxyService();
    }
}
