package top.yelinsheng.torhop.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import top.yelinsheng.torhop.cmdoption.SlaveCmdOption;
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class Slave extends Proxy {
    public static final Logger logger = LogManager.getLogger("Slave");

    public Slave(Router router) {
        super(router);
    }

    public static void main(String[] args) {
        SlaveCmdOption option  = new SlaveCmdOption();
        CmdLineParser parser = new CmdLineParser(option);

        if (args.length == 0){
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
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
        } catch (CmdLineException cle){
            System.out.println("Command line error: " + cle.getMessage());
            parser.printUsage(System.out);
            return;
        }
    }

}
