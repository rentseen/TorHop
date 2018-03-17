package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.Router;

public abstract class Proxy {
    protected Router router;

    public Proxy(Router router) {
        this.router = router;
    }
    public void setRouter(Router router) {
        this.router = router;
    }
    public void startProxyService() {
        router.startService();
        router.registerService();
    }
}
