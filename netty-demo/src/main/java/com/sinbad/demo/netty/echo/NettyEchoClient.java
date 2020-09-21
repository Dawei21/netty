package com.sinbad.demo.netty.echo;

import com.sinbad.demo.netty.decoder.MsgpackDecoder;
import com.sinbad.demo.netty.encoder.MsgpackEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author sinbad on 2020/09/20.
 */

public class NettyEchoClient {


	public void connect(String address, int port) throws InterruptedException {

		EventLoopGroup clientHandleGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();

		try {

			bootstrap
					.group(clientHandleGroup)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {

							socketChannel.pipeline().addLast("Length frameDecoder",
									new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
							//为啥这个么写呢 右编码 又解码 因为双向通道 接受和发出消息得都做 当然pipeline会区分进出的
							//解码
							socketChannel.pipeline().addLast("msgpack decoder", new MsgpackDecoder());

							socketChannel.pipeline().addLast("Length frameEncoder", new LengthFieldPrepender(2));

							//编码
							socketChannel.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
//							ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//							socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
//							socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
//							socketChannel.pipeline().addLast(new StringDecoder());
							socketChannel.pipeline().addLast(new EchoClientHandler());
						}
					});


			ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
			channelFuture.channel().closeFuture().sync();

		} finally {
			clientHandleGroup.shutdownGracefully();
		}
	}


	public static void main(String[] args) throws InterruptedException {
		new NettyEchoClient().connect("127.0.0.1", 8090);
	}

}
