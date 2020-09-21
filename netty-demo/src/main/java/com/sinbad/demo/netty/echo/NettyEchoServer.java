package com.sinbad.demo.netty.echo;

import com.sinbad.demo.netty.decoder.MsgpackDecoder;
import com.sinbad.demo.netty.encoder.MsgpackEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author sinbad on 2020/09/20.
 */

public class NettyEchoServer {


	public void bind(int port) throws InterruptedException {
		EventLoopGroup acceptGroup = new NioEventLoopGroup();
		EventLoopGroup clientHandleGroup = new NioEventLoopGroup();


		ServerBootstrap bootstrap = new ServerBootstrap();

		try {

			bootstrap
					.group(acceptGroup, clientHandleGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {

							socketChannel.pipeline().addLast("length frame Decoder",
									new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
							//为啥这个么写呢 因为双向通道 接受和发出消息得都做 当然pipeline会区分进出的
							socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());

							socketChannel.pipeline().addLast("length framd encoder", new LengthFieldPrepender(2));

							socketChannel.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
//							ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//							socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
//							socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
//							socketChannel.pipeline().addLast(new StringDecoder());

							socketChannel.pipeline().addLast(new EchoServiceHandler());
						}
					});


			ChannelFuture channelFuture = bootstrap.bind(port).sync();
			channelFuture.channel().closeFuture().sync();

		} finally {
			acceptGroup.shutdownGracefully();
			clientHandleGroup.shutdownGracefully();
		}
	}


	public static void main(String[] args) throws InterruptedException {
		new NettyEchoServer().bind(8090);
	}

}
