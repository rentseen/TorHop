package top.yelinsheng.torhop.utils;

public class Address {
    private final String host;
    private final int port;
    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    @Override
    public String toString() {
        return host+":"+port;
    }
}
