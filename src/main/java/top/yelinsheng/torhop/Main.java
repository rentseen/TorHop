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
            if(option.leaderAddress==null || option.proxyAddress==null) {
                logger.error("Please specify the leader address and proxy address!");
                parser.printUsage(System.out);
            }
            else {
                String[] leaderList = option.leaderAddress.split(":");
                String[] proxyList = option.proxyAddress.split(":");
                if(leaderList.length!=2) {
                    logger.error("leader address format error!");
                }
                else if(proxyList.length!=2) {
                    logger.error("proxy address format error!");
                }
                else {
                    String leaderHost = leaderList[0];
                    int leaderPort = Integer.parseInt(leaderList[1]);
                    String proxyHost = proxyList[0];
                    int proxyPort = Integer.parseInt(proxyList[1]);
                    if(option.role.equals("leader")) {
                        final Address leaderAddress = new Address(leaderHost, leaderPort);
                        final Address slaveProxyAddress = new Address(proxyHost, proxyPort);
                        Router leaderRouter = new DefaultRouter(slaveProxyAddress, null, leaderAddress, "slave");
                        Leader leader = new Leader(leaderRouter);
                        leader.startLeaderService();
                        leader.startProxyService();
                    }
                    else if(option.role.equals("slave")) {
                        final Address slaveProxyAddress = new Address(proxyHost, proxyPort);
                        final Address leaderAddress = new Address(leaderHost, leaderPort);
                        Router slaveRouter = new DefaultRouter(slaveProxyAddress, null, leaderAddress, "slave");
                        Slave slave = new Slave(slaveRouter);
                        slave.startProxyService();
                    }
                    else if(option.role.equals("gateway")) {
                        final Address gateWayAddress = new Address(proxyHost, proxyPort);
                        final Address leaderAddress = new Address(leaderHost, leaderPort);
                        Router gateWayRouter = new DefaultRouter(gateWayAddress, null, leaderAddress, "gateWay");
                        GateWay gateWay = new GateWay(gateWayRouter);
                        gateWay.startProxyService();
                    }
                    else {
                        logger.error("role should be: leader, slave, gateway");
                    }
                }
            }
        } catch (CmdLineException cle){
            System.out.println("Command line error: " + cle.getMessage());
            parser.printUsage(System.out);
            return;
        }
    }
}
