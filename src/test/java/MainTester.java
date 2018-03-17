import top.yelinsheng.torhop.proxy.Leader;
import top.yelinsheng.torhop.proxy.Slave;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;

public class MainTester {
    public static void main(String[] args) {
        final Address slaveProxyAddress = new Address("127.0.0.1", 9999);
        final Address leaderProxyAddress = new Address("127.0.0.1", 7777);
        final Address leaderAddress = new Address("127.0.0.1", 8888);
        Router slaveRouter = new DefaultRouter(slaveProxyAddress, leaderProxyAddress, leaderAddress, "slave");
        Router leaderRouter = new DefaultRouter(leaderProxyAddress, null, leaderAddress, "leader");
        Slave slave = new Slave(slaveRouter);
        Leader leader = new Leader(leaderRouter);
        leader.startLeaderService();
        leader.startProxyService();
        slave.startProxyService();
    }
}
