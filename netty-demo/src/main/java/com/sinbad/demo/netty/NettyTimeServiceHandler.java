package com.sinbad.demo.netty;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyTimeServiceHandler extends ChannelHandlerAdapter {

	/**
	 * 接收到请求就做处理
	 */
	@Override
	public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg)
			throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;

		int readableBytes = byteBuf.readableBytes();
		byte[] requestBuf = new byte[readableBytes];

		byteBuf.readBytes(requestBuf);

		String body = new String(requestBuf, "UTF-8");

		System.out.println("Get order : " + body);

		String responseBody = "Bad Request";
		if ("GET TIME".equalsIgnoreCase(body)) {
			responseBody = new Date().toString();
		}
		responseBody = responseBody + System.getProperty("line.separator");

		ByteBuf responseBuf = Unpooled.copiedBuffer(responseBody.getBytes());
		channelHandlerContext.write(responseBuf);
	}

	/**
	 * 处理完成 就返回
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
		channelHandlerContext.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause)
			throws Exception {
		cause.fillInStackTrace();
		channelHandlerContext.close();
	}
}
