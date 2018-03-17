package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class GateWay extends Proxy {
    public GateWay(Router router) {
        super(router);
    }

    public static void main(String[] args) {
        final Address gateWayAddress = new Address("127.0.0.1", 8000);

        final Address leaderAddress = new Address("127.0.0.1", 7000);

        Router gateWayRouter = new DefaultRouter(gateWayAddress, null, leaderAddress, "gateWay");
        GateWay gateWay = new GateWay(gateWayRouter);
        gateWay.startProxyService();
    }
}
