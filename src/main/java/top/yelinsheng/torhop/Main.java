package top.yelinsheng.torhop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import top.yelinsheng.torhop.cmdoption.CmdOption;
import top.yelinsheng.torhop.proxy.GateWay;
import top.yelinsheng.torhop.proxy.Leader;
import top.yelinsheng.torhop.proxy.Slave;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class Main {
    public static final Logger logger = LogManager.getLogger("Main");
    public static void main(String[] args) {
        CmdOption option  = new CmdOption();
        CmdLineParser parser = new CmdLineParser(option);

        if (args.length == 0){
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
            if(option.role.equals("leader")) {
                if(option.leaderPort<0 || option.proxyPort<0) {
                    logger.error("Please specify the leader port and proxy port!");
                    parser.printUsage(System.out);
                }
                else {
                    final Address leaderAddress = new Address("127.0.0.1", option.leaderPort);
                    final Address slave4ProxyAddress = new Address("127.0.0.1", option.proxyPort);
                    Router leaderRouter = new DefaultRouter(slave4ProxyAddress, null, leaderAddress, "slave");
                    Leader leader = new Leader(leaderRouter);
                    leader.startLeaderService();
                    leader.startProxyService();
                }
            }
            else if(option.role.equals("slave")) {
                if(option.leaderAddress==null || option.proxyPort<0) {
                    logger.error("Please specify the leader address and proxy port!");
                    parser.printUsage(System.out);
                }
                else {
                    String[] tmp = option.leaderAddress.split(":");
                    if(tmp.length!=2) {
                        logger.error("leader address format error!");
                    }
                    else {
                        String leaderHost = tmp[0];
                        int leaderPort = Integer.parseInt(tmp[1]);
                        final Address slaveProxyAddress = new Address("127.0.0.1", option.proxyPort);
                        final Address leaderAddress = new Address(leaderHost, leaderPort);
                        Router slaveRouter = new DefaultRouter(slaveProxyAddress, null, leaderAddress, "slave");
                        Slave slave = new Slave(slaveRouter);
                        slave.startProxyService();
                    }
                }
            }
            else if(option.role.equals("gateway")) {
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
            }
            else {
                logger.error("role should be: leader, slave, gateway");
            }
        } catch (CmdLineException cle){
            System.out.println("Command line error: " + cle.getMessage());
            parser.printUsage(System.out);
            return;
        }
    }
}
