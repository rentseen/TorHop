package top.yelinsheng.torhop.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import top.yelinsheng.torhop.utils.Address;

public class DefaultRouterClientHandler extends ChannelInboundHandlerAdapter {
    private Channel clientChannel;
    private final Address proxyAddress;

    public DefaultRouterClientHandler(Channel clientChannel, Address proxyAddress) {
        this.clientChannel = clientChannel;
        this.proxyAddress = proxyAddress;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        //修改http响应体返回至客户端

        HttpHeaders httpHeaders = response.headers();
        if(httpHeaders.contains("torhop-tag")) {
            String s = httpHeaders.get("torhop-tag");
            httpHeaders.set("torhop-tag", proxyAddress.hashCode() + "->" + s);
        }
        else {
            httpHeaders.add("torhop-tag", proxyAddress.hashCode());
        }
        final ChannelFuture cf = clientChannel.writeAndFlush(msg);
        cf.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
