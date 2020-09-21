
package com.sinbad.demo.netty.echo;

import java.util.UUID;

import com.sinbad.demo.netty.module.MessageData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServiceHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

		System.out.println("Get request  message :" + msg.toString());


		MessageData messageData = new MessageData(System.currentTimeMillis(), "Service Send : " + UUID.randomUUID().toString());
		System.out.println("Send response  message :" + messageData.toString());
		channelHandlerContext.writeAndFlush(messageData);

//		String body = (String) msg;
//		body += "[review]$_";
//		ByteBuf byteBuf = Unpooled.copiedBuffer(body.getBytes());
//		channelHandlerContext.writeAndFlush(byteBuf);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
		cause.printStackTrace();

		channelHandlerContext.close();

	}
}
