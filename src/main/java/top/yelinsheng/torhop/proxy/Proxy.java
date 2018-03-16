package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.Router;

public abstract class Proxy {
    protected Router router;
    protected HeartBeat heartBeat;

    public Proxy(Router router, HeartBeat heartBeat) {
        this.router = router;
        this.heartBeat = heartBeat;
    }
    public abstract void reset();
}
