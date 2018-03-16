package top.yelinsheng.torhop.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import top.yelinsheng.torhop.utils.Address;

public class DefaultRouterServerHandler extends ChannelInboundHandlerAdapter {
    private Address nextHopAddress;
    public DefaultRouterServerHandler(Address nextHopAddress) {
        this.nextHopAddress = nextHopAddress;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String host;
            int port;

            if (nextHopAddress == null) {
                //此节点为出口

                String[] temp = request.headers().get("host").split(":");
                host = temp[0];
                port = 80;
                if (temp.length > 1) {
                    port = Integer.parseInt(temp[1]);
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
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(6553600));
                            ch.pipeline().addLast(new DefaultProxyClientHandler(ctx.channel()));
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
