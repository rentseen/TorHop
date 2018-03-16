package top.yelinsheng.torhop.router;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.handler.DefaultRouterServerHandler;

public class DefaultRouter implements Router {
    private Address myAddress;
    private Address nextHopAddress;

    public DefaultRouter(Address myAddress, Address nextHopAddress) {
        this.myAddress = myAddress;
        this.nextHopAddress = nextHopAddress;
    }

    public void startService() {
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
                            pipeline.addLast("codec", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(6553600));
                            pipeline.addLast("handler", new DefaultRouterServerHandler(nextHopAddress));
                        }});

            ChannelFuture future = bootstrap.bind(myAddress.getPort()).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setNextHop(Address nextHopAddress) {
        this.nextHopAddress = nextHopAddress;
    }
}
