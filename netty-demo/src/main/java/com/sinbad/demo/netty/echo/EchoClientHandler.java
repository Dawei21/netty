package com.sinbad.demo.netty.echo;

import java.util.UUID;

import com.sinbad.demo.netty.module.MessageData;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

	private static final String TEST_MESSAGE = "Say hello!$_";

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {

		for (int i = 0; i < 10; i++) {
			MessageData messageData = new MessageData(System.currentTimeMillis(), "Send request msg:" + UUID.randomUUID().toString());
			channelHandlerContext.write(messageData);
			//channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(TEST_MESSAGE.getBytes()));
		}
		channelHandlerContext.flush();
	}


	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

		System.out.println("client review msg: " + msg.toString());
		//channelHandlerContext.write(msg);
//		String reviewMsg = (String) msg;
//		System.out.println(reviewMsg);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
		channelHandlerContext.flush();
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {

		cause.printStackTrace();

		channelHandlerContext.close();

	}
}
