package com.sinbad.demo.netty.module;

import org.msgpack.annotation.Message;

/**
 * 消息数据
 */
@Message
public class MessageData {

	private long curTime;

	private String uuid;

	public long getCurTime() {
		return curTime;
	}

	public void setCurTime(long curTime) {
		this.curTime = curTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public MessageData() {
	}

	public MessageData(long curTime, String uuid) {
		this.curTime = curTime;
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "MessageData{" +
				"curTime=" + curTime +
				", uuid='" + uuid + '\'' +
				'}';
	}
}
