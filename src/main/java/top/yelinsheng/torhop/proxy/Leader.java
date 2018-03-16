package top.yelinsheng.torhop.proxy;

import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.heartbeat.HeartBeat;
import top.yelinsheng.torhop.router.Router;

import java.util.List;

public class Leader extends Proxy {
    List<Address> slaveList;

    public Leader(Router router, HeartBeat heartBeat) {
        super(router, heartBeat);
    }

    public void reset() {

    }
}
