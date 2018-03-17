package top.yelinsheng.torhop.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.handler.LeaderServiceHandler;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leader extends Slave {
    public static final Logger logger = LogManager.getLogger("Leader");
    private List<Address> slaveList;
    private Map<Address, ChannelHandlerContext> slaveChannelMap;
    public Leader(Router router) {
        super(router);
        slaveList = new ArrayList<Address>();
        slaveChannelMap = new HashMap<Address, ChannelHandlerContext>();
    }
    public void startLeaderService() {
        new Thread() {
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 100)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(new ChannelInitializer<SocketChannel>(){
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    //解决粘包问题
                                    pipeline.addLast(new LineBasedFrameDecoder(1024));
                                    pipeline.addLast(new StringDecoder());
                                    pipeline.addLast("handler", new LeaderServiceHandler());
                                }});
                    ChannelFuture future = bootstrap.bind(router.getLeaderAddress().getPort()).sync();
                    future.channel().closeFuture().sync();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }.start();
    }
    public synchronized void addSlave(Address address, ChannelHandlerContext channel) {
        slaveList.add(address);
        slaveChannelMap.put(address, channel);
        logger.error(slaveList);
    }
    public synchronized void removeSlave(Address address) {
        slaveList.remove(address);
        slaveChannelMap.remove(address);
        logger.error(slaveList);
    }
}
