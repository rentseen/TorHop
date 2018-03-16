package top.yelinsheng.torhop.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultProxyClientHandler extends ChannelInboundHandlerAdapter {
    private Channel clientChannel;

    public DefaultProxyClientHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        //修改http响应体返回至客户端
        response.headers().add("torhop-tag","torhop");
        final ChannelFuture cf = clientChannel.writeAndFlush(msg);
        cf.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
