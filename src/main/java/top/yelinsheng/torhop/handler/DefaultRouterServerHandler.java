package top.yelinsheng.torhop.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.utils.Address;

public class DefaultRouterServerHandler extends ChannelInboundHandlerAdapter {
    private Address nextHopAddress;
    private final Address proxyAddress;
    public static final Logger logger = LogManager.getLogger("DefaultRouterServerHandler");

    public DefaultRouterServerHandler(Address nextHopAddress, Address proxyAddress) {
        this.nextHopAddress = nextHopAddress;
        this.proxyAddress = proxyAddress;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String host;
            int port;

            if (nextHopAddress == null) {
                //此节点为出口

                String[] tmp = request.headers().get("host").split(":");
                if(tmp==null || tmp.length==0) {
                    logger.error(request.headers().toString());
                    return;
                }
                host = tmp[0];
                port = 80;
                if (tmp.length > 1) {
                    port = Integer.parseInt(tmp[1]);
                } else {
                    if (request.uri().indexOf("https") == 0) {
                        port = 443;
                    }
                }
            } else {
                host = nextHopAddress.getHost();
                port = nextHopAddress.getPort();
            }

            Bootstrap bootstrap = new Bootstrap();
            //使用现有的eventloop避免创建线程池的资源消耗
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(6553600));
                            ch.pipeline().addLast(new DefaultRouterClientHandler(ctx.channel(), proxyAddress));
                        }
                    });

            ChannelFuture cf = bootstrap.connect(host, port);
            cf.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        ctx.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
