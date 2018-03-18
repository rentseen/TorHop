package top.yelinsheng.torhop.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.router.Router;

public class Slave extends Proxy {
    public static final Logger logger = LogManager.getLogger("Slave");

    public Slave(Router router) {
        super(router);
    }

}
