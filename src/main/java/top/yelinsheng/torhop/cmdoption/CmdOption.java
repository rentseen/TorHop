package top.yelinsheng.torhop.cmdoption;

import org.kohsuke.args4j.Option;

public class CmdOption {
    @Option(name="-role", required = true, usage="Specify role: leader, slave, gateway")
    public String role = null;

    @Option(name="-leaderport", required = false, usage="Specify leader port")
    public int leaderPort = -1;

    @Option(name="-proxyport", required = true, usage="Specify proxy port")
    public int proxyPort = -1;

    @Option(name="-leaderaddress", required = false, usage="Specify leader address, format: ip:port")
    public String leaderAddress = null;

}
