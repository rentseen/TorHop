package top.yelinsheng.torhop.router;

import top.yelinsheng.torhop.utils.Address;

public interface Router {
    void startService();
    void setNextHop(Address nextHopAddress);
}
