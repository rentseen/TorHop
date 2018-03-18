package top.yelinsheng.torhop.cmdoption;

import org.kohsuke.args4j.Option;

public class CmdOption {
    @Option(name="-role", required = true, usage="Specify role: leader, slave, gateway")
    public String role = null;

    @Option(name="-proxyaddress", required = true, usage="Specify proxy address, format: ip:port")
    public String proxyAddress = null;

    @Option(name="-leaderaddress", required = true, usage="Specify leader address, format: ip:port")
    public String leaderAddress = null;

}
