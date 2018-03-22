package top.yelinsheng.torhop.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.router.Router;
import top.yelinsheng.torhop.utils.Address;

public class RegisterHandler extends ChannelInboundHandlerAdapter {
    public static final Logger logger = LogManager.getLogger("RegisterHandler");
    private Router router;
    public RegisterHandler(Router router) {
        this.router = router;
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        router.registerService();
    }

    //heartbeat
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE) {
                //logger.error("send heartbeat");
                ByteBuf encoded = Unpooled.copiedBuffer("heartbeat\n".getBytes());
                ctx.channel().writeAndFlush(encoded);
            }
            else if(event.state() == IdleState.READER_IDLE) {
                logger.error("ERROR: channel failed and shutdown");
                ctx.channel().close();
                router.registerService();
            }
        }
    }

    //将自己注册到leader服务器
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String msg = "register:" + router.getRole() + ":" + router.getProxyAddress().toString()+"\n";
        ByteBuf encoded = Unpooled.copiedBuffer(msg.getBytes());
        ctx.writeAndFlush(encoded);
    }

    //接收leader的更新数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof String) {
            String hostPath = (String) msg;
            String[] tmp = hostPath.split(":");
            if(tmp[0].equals("nextHop")) {
                Address address = null;
                if(!tmp[1].equals("null")) {
                    address = new Address(tmp[1], Integer.parseInt(tmp[2]));
                }
                router.setNextHop(address);
                logger.error(router.getProxyAddress()+" next hop: " + address);
            }
            else if(tmp[0].equals("heartbeatACK")) {
                //logger.error("get heartbeat ack");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
