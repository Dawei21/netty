package com.sinbad.demo.gateway.application;

import com.sinbad.demo.gateway.handler.NettyHttpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author sinbad
 * Created on 2021-04-20
 */
public class NettyServiceApplication {


    private final int inetPort;

    public NettyServiceApplication(int inetPort) {
        this.inetPort = inetPort;
    }

    public static void main(String[] args) throws Exception {
        NettyServiceApplication nettyServiceApplication = new NettyServiceApplication(8881);
        nettyServiceApplication.start();
    }


    private void start() throws InterruptedException {

        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(10);
        EventLoopGroup workEventLoopGroup = new NioEventLoopGroup(10);
        try {


            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // 1. 绑定两个线程组分别用来处理客户端通道的accept和读写时间
                    .group(bossEventLoopGroup, workEventLoopGroup)
                    // 2. 绑定服务端通道NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 3. 给读写事件的线程通道绑定handler去真正处理读写
                    // ChannelInitializer初始化通道SocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // 请求解码器
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 将HTTP消息的多个部分合成一条完整的HTTP消息
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            // 响应转码器
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // 解决大码流的问题，ChunkedWriteHandler：向客户端发送HTML5文件
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            // 自定义处理handler
                            socketChannel.pipeline().addLast("http-server", new NettyHttpServerHandler());
                        }
                    });
            // 4. 监听端口（服务器host和port端口），同步返回
            // ChannelFuture future = server.bind(inetHost, this.inetPort).sync();
            ChannelFuture future = serverBootstrap.bind(this.inetPort).sync();
            // 当通道关闭时继续向后执行，这是一个阻塞方法
            future.channel().closeFuture().sync();
        } finally {
            workEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup.shutdownGracefully();
        }
    }
}
