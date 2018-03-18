package top.yelinsheng.torhop.cmdoption;

import org.kohsuke.args4j.Option;

public class LeaderCmdOption {
    @Option(name="-leaderPort", usage="Specify leader port")
    public int leaderPort = -1;

    @Option(name="-proxyPort", usage="Specify proxy port")
    public int proxyPort = -1;
}
