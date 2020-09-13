package com.sinbad.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyTimeClient {

	private static final int SOCKET_BLOCK_LOG = 1024;
	private static final int SERVER_PORT = 8002;
	private static final String SERVER_ADDRESS = "127.0.0.1";

	public static void main(String[] args) throws InterruptedException {
		new NettyTimeClient().startConnectService(SERVER_PORT, SERVER_ADDRESS);
	}

	public void startConnectService(int port, String host) throws InterruptedException {
		//客户端 负责I/O的线程组
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline().addLast(new NettyTimeClientHandler());
						}
					});

			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

			System.out.println("finished connect wait do working ");

			//等待客户端链路关闭
			channelFuture.channel().closeFuture().sync();

			System.out.println("Client  Close ");

		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

}
