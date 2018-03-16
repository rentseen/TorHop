import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;

public class DefaultRouterTester {
    public static void main(String[] args) {
        final Address gateWayAddress = new Address("127.0.0.1", 8888);
        final Address outAddress = new Address("127.0.0.1", 9999);
        new Thread(){
            public void run() {
                Router routerGateWay = new DefaultRouter(gateWayAddress, outAddress);
                routerGateWay.start();
            }
        }.start();
        new Thread(){
            public void run() {
                Router routerOut = new DefaultRouter(outAddress, null);
                routerOut.start();
            }
        }.start();
    }
}
