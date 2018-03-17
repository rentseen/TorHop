package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.Router;

public class Slave extends Proxy {

    public Slave(Router router) {
        super(router);
    }

}
