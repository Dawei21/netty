package com.sinbad.demo.netty.decoder;

import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {


	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

		final byte[] messageAry;
		final int length = byteBuf.readableBytes();
		messageAry = new byte[length];

		//从输入的流中读取需要解码的 码流
		byteBuf.getBytes(byteBuf.readerIndex(), messageAry, 0, length);

		MessagePack messagePack = new MessagePack();

		//msgpack.register(User.class);//与 @Message注解用其一即可
		//反序列化数据
		Value read = messagePack.read(messageAry);
		//存储反序列化的数据内容
		list.add(read);


	}
}
