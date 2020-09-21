package com.sinbad.demo.netty.encoder;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {


	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {

		//进行数据的序列化
		MessagePack messagePack = new MessagePack();
		byte[] writeByte = messagePack.write(object);

		byteBuf.writeBytes(writeByte);

	}
}
