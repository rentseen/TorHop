package top.yelinsheng.torhop.router;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.handler.RegisterHandler;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.handler.DefaultRouterServerHandler;

import java.util.concurrent.TimeUnit;

public class DefaultRouter extends Router {
    public static final Logger logger = LogManager.getLogger("defaultRouter");
    public DefaultRouter(Address proxyAddress, Address nextHopAddress, Address leaderAddress, String role) {
        super(proxyAddress, nextHopAddress, leaderAddress, role);
    }

    public void registerService() {
        new Thread() {
            public void run() {
                registerConnect();
            }
        }.start();

    }

    public void startService() {
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
                                    pipeline.addLast("codec", new HttpServerCodec());
                                    pipeline.addLast("aggregator", new HttpObjectAggregator(6553600));
                                    pipeline.addLast("handler", new DefaultRouterServerHandler(nextHopAddress, proxyAddress));
                                }});
                    ChannelFuture future = bootstrap.bind(proxyAddress.getPort()).sync();
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

    public void registerConnect() {
        try {
            final RegisterHandler registerHandler = new RegisterHandler(this);
            final EventLoopGroup workerGroup = new NioEventLoopGroup();
            final Bootstrap bootstrap = new Bootstrap();
            logger.error( proxyAddress.toString() + " try to register to leader: " + leaderAddress.toString());
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //解决粘包问题
                            pipeline.addLast(new LineBasedFrameDecoder(1024));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(registerHandler);
                        }
                    });

            ChannelFuture cf = bootstrap.connect(leaderAddress.getHost(), leaderAddress.getPort()).
                    addListener(new RegisterConnectionListener(this)).sync();
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class RegisterConnectionListener implements ChannelFutureListener {
    private DefaultRouter router;
    public RegisterConnectionListener(DefaultRouter router) {
        this.router = router;
    }
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                public void run() {
                    router.registerConnect();
                }
            }, 1L, TimeUnit.SECONDS);
        }
        else {
            DefaultRouter.logger.error(router.getProxyAddress().toString() + " register to leader successfully");
        }
    }
}
