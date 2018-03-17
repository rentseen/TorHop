package top.yelinsheng.torhop.router;

import top.yelinsheng.torhop.utils.Address;

public abstract class Router {
    protected Address proxyAddress;
    protected Address nextHopAddress;
    protected String role;
    protected Address leaderAddress;

    public Router(Address proxyAddress, Address nextHopAddress, Address leaderAddress, String role) {
        this.proxyAddress = proxyAddress;
        this.nextHopAddress = nextHopAddress;
        this.leaderAddress = leaderAddress;
        this.role = role;
    }

    public abstract void registerService();
    public abstract void startService();
    public void setNextHop(Address nextHopAddress) {
        this.nextHopAddress = nextHopAddress;
    }
    public String getRole() {
        return role;
    }
    public Address getLeaderAddress() {
        return leaderAddress;
    }
    public Address getProxyAddress() {
        return proxyAddress;
    }
}
