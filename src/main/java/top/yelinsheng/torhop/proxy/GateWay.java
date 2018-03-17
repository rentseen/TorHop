package top.yelinsheng.torhop.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import top.yelinsheng.torhop.CmdOption.GateWayCmdOption;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class GateWay extends Proxy {
    public static final Logger logger = LogManager.getLogger("GateWay");
    public GateWay(Router router) {
        super(router);
    }

    public static void main(String[] args) {
        GateWayCmdOption option = new GateWayCmdOption();
        CmdLineParser parser = new CmdLineParser(option);

        if (args.length == 0) {
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
            if (option.leaderAddress == null || option.proxyPort < 0) {
                logger.error("Please specify the leader address and proxy port!");
                parser.printUsage(System.out);
            } else {
                String[] tmp = option.leaderAddress.split(":");
                if (tmp.length != 2) {
                    logger.error("leader address format error!");
                }
                else {
                    String leaderHost = tmp[0];
                    int leaderPort = Integer.parseInt(tmp[1]);
                    final Address gateWayAddress = new Address("127.0.0.1", option.proxyPort);
                    final Address leaderAddress = new Address(leaderHost, leaderPort);
                    Router gateWayRouter = new DefaultRouter(gateWayAddress, null, leaderAddress, "gateWay");
                    GateWay gateWay = new GateWay(gateWayRouter);
                    gateWay.startProxyService();
                }
            }
        } catch (CmdLineException cle) {
            System.out.println("Command line error: " + cle.getMessage());
            parser.printUsage(System.out);
            return;
        }
    }
}
