package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.Router;

public class GateWay extends Proxy {
    public GateWay(Router router) {
        super(router);
    }
}
