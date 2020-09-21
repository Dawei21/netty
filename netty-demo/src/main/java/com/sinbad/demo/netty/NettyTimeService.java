package com.sinbad.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 时间服务器 服务端
 */
public class NettyTimeService {

	/**
	 * 传统的jdk编写异步I/O步骤如下 1、创建ServerSocketChannel，配置他为非阻塞模式 2、绑定监听，配置TCP参数，例如backlog大小
	 * 3、创建一个独立的I/O线程，用于轮询多路复用器Selector 4、创建Selector，将之前创建的ServerSocketChannel
	 * 注册到Selector上，监听SelectionKey.ACCEPT (请求事件) 5、启动I/O线程，再循环体内执行Selector.select(), 轮询就绪的Channel
	 * 6、当轮询到了处于就绪状态的Channel时，需要对其进行判断，如果是在OP_ACCEPT状态，说明是有客户端接入，则调用SelectorSocketChannel.accept()方法接受新的客户端
	 * 7、设置新接入的客户端链路SocketChannel为非阻塞模式，配置其他的一些Tcp参数 8、将SocketChannel注册到Selector，监听OP_READ操作位。
	 * 9、如果轮询Channel为OP_READ,则说明SocketChannel中有新的就绪数据包需要读取，则构造ByteBuffer对象读取数据包
	 * 10、轮询到Channel为OP_WRITE,说明还有数据没有发送完，需要继续发送。
	 */

	private static final int SOCKET_BLOCK_LOG = 1024;
	private static final int SERVER_PORT = 8002;

	public static void main(String[] args) throws Exception {

		new NettyTimeService().startOpenService(SERVER_PORT);

	}

	/**
	 */
	public void startOpenService(int port) throws Exception {
		// 配置服务端的NIO线程组
		// bossGroup: 用作接受客户端请求的线程组 （acceptor）
		// workerGroup: 用作服务器处理业务I/O的线程组 （client）
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();

			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, SOCKET_BLOCK_LOG)
					.childHandler(new ChildChannelHandler());
			// Channel绑定端口
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

			System.out.println("Finish Bind port, start working  ..... .... ... .. . ");
			// 等待服务端端口关闭
			channelFuture.channel().closeFuture().sync();

			System.out.println("Finish");
		} finally {

			// 释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		protected void initChannel(SocketChannel socketChannel) throws Exception {

			//
			socketChannel.pipeline().addLast(new LineBasedFrameDecoder(SOCKET_BLOCK_LOG));
			socketChannel.pipeline().addLast(new StringDecoder());
			socketChannel.pipeline().addLast(new NettyTimeServiceHandler());

		}
	}

}
