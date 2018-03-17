package top.yelinsheng.torhop.CmdOption;

import org.kohsuke.args4j.Option;

public class GateWayCmdOption {
    @Option(name="-leaderAddress", usage="Specify leader address, format: ip:port")
    public String leaderAddress = null;

    @Option(name="-proxyPort", usage="Specify proxy port")
    public int proxyPort = -1;
}
