package top.yelinsheng.torhop.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
import top.yelinsheng.torhop.router.DefaultRouter;
import top.yelinsheng.torhop.utils.Address;
import top.yelinsheng.torhop.router.Router;

import java.util.*;

public class Leader extends Slave {
    public static final Logger logger = LogManager.getLogger("Leader");
    private List<Address> slaveList;
    private Map<Address, ChannelHandlerContext> slaveChannelMap;
    private List<Address> gateWayList;
    private Map<Address, ChannelHandlerContext> gateWayChannelMap;
    public Leader(Router router) {
        super(router);
        slaveList = new ArrayList<Address>();
        slaveChannelMap = new HashMap<Address, ChannelHandlerContext>();
        gateWayList = new ArrayList<Address>();
        gateWayChannelMap = new HashMap<Address, ChannelHandlerContext>();
    }
    public void startLeaderService() {
        final Leader leader = this;
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
                                    pipeline.addLast("handler", new LeaderServiceHandler(leader));
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
    public synchronized void addSlave(Address address, ChannelHandlerContext ctx) {
        slaveList.add(address);
        slaveChannelMap.put(address, ctx);
        generateRoute();
    }
    public synchronized void removeSlave(Address address) {
        slaveList.remove(address);
        slaveChannelMap.remove(address);
        generateRoute();
    }
    public synchronized void removeSlave(ChannelHandlerContext ctx) {
        Iterator<Map.Entry<Address, ChannelHandlerContext>> iterator = slaveChannelMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Address, ChannelHandlerContext> entry = iterator.next();
            if(entry.getValue()==ctx) {
                Address address = entry.getKey();
                iterator.remove();
                slaveList.remove(address);
                generateRoute();
                break;
            }
        }
    }
    public synchronized void addGateWay(Address address, ChannelHandlerContext ctx) {
        gateWayList.add(address);
        gateWayChannelMap.put(address, ctx);
        if(slaveList.size()>0) {
            Address headAddress = slaveList.get(0);
            String msg = "nextHop:"+headAddress+"\n";
            ByteBuf encoded = Unpooled.copiedBuffer(msg.getBytes());
            ctx.writeAndFlush(encoded);
        }
        logger.error("gateWay list: " + gateWayList);
    }
    public synchronized void removeGateWay(Address address) {
        gateWayList.remove(address);
        gateWayChannelMap.remove(address);
        logger.error("gateWay list: " + gateWayList);
    }
    public synchronized void removeGateWay(ChannelHandlerContext ctx) {
        Iterator<Map.Entry<Address, ChannelHandlerContext>> iterator = gateWayChannelMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Address, ChannelHandlerContext> entry = iterator.next();
            if(entry.getValue()==ctx) {
                Address address = entry.getKey();
                iterator.remove();
                gateWayList.remove(address);
                logger.error("gateWay list: " + gateWayList);
                break;
            }
        }
    }
    public synchronized void generateRoute() {
        Collections.shuffle(slaveList);
        logger.error("slave list after shuffle: " + slaveList);
        for(int i=0; i<slaveList.size()-1; i++) {
            ChannelHandlerContext ctx = slaveChannelMap.get(slaveList.get(i));
            String msg = "nextHop:"+slaveList.get(i+1)+"\n";
            ByteBuf encoded = Unpooled.copiedBuffer(msg.getBytes());
            ctx.writeAndFlush(encoded);
        }
        slaveChannelMap.get(slaveList.get(slaveList.size()-1)).
                writeAndFlush(Unpooled.copiedBuffer("nextHop:null:null\n".getBytes()));
        Address headAddress = slaveList.get(0);
        for(int i=0; i<gateWayList.size(); i++) {
            ChannelHandlerContext ctx = gateWayChannelMap.get(gateWayList.get(i));
            String msg = "nextHop:"+headAddress+"\n";
            ByteBuf encoded = Unpooled.copiedBuffer(msg.getBytes());
            ctx.writeAndFlush(encoded);
        }
    }

    public static void main(String[] args) {
        final Address leaderAddress = new Address("127.0.0.1", 7000);
        final Address slave4ProxyAddress = new Address("127.0.0.1", 9004);
        Router leaderRouter = new DefaultRouter(slave4ProxyAddress, null, leaderAddress, "slave");
        Leader leader = new Leader(leaderRouter);
        leader.startLeaderService();
        leader.startProxyService();
    }
}
