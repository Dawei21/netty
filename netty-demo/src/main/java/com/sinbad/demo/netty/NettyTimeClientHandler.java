package com.sinbad.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyTimeClientHandler extends ChannelHandlerAdapter {

	private final ByteBuf waitSendMessage;

	public NettyTimeClientHandler() {
		byte[] requestBody = "GET TIME".getBytes();
		waitSendMessage = Unpooled.buffer(requestBody.length);
		waitSendMessage.writeBytes(requestBody);
	}

	/**
	 * 连接上就写请求并发出去
	 */
	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		System.out.println("channel Active");
		channelHandlerContext.writeAndFlush(waitSendMessage);
	}

	/**
	 * 接受到响应数据就解析
	 */
	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg)
			throws Exception {
		ByteBuf responseBuf = (ByteBuf) msg;
		int readableBytes = responseBuf.readableBytes();
		byte[] responseByte = new byte[readableBytes];
		responseBuf.readBytes(responseByte);

		String responseBody = new String(responseByte, "UTF-8");
		System.out.println("Get time is : " + responseBody);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause)
			throws Exception {
		cause.fillInStackTrace();
		channelHandlerContext.close();
	}
}
