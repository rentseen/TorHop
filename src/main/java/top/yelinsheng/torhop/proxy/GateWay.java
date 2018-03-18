package top.yelinsheng.torhop.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.router.Router;

public class GateWay extends Proxy {
    public static final Logger logger = LogManager.getLogger("GateWay");
    public GateWay(Router router) {
        super(router);
    }
}
