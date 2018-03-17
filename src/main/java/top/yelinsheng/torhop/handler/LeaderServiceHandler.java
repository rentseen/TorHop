package top.yelinsheng.torhop.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yelinsheng.torhop.proxy.Leader;
import top.yelinsheng.torhop.utils.Address;

public class LeaderServiceHandler extends ChannelInboundHandlerAdapter {
    public static final Logger logger = LogManager.getLogger("LeaderServiceHandler");
    Leader leader;

    public LeaderServiceHandler(Leader leader) {
        this.leader = leader;
    }

    //接收leader的更新数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof String) {
            String line = (String) msg;
            String[] tmp = line.split(":");
            logger.error(line);
            if(tmp[0].equals("register")) {
                String role = tmp[1];
                Address address = new Address(tmp[2], Integer.parseInt(tmp[3]));
                if(role.equals("gateWay")) {
                    leader.addGateWay(address, ctx);
                }
                else if(role.equals("slave")) {
                    leader.addSlave(address, ctx);
                }

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
