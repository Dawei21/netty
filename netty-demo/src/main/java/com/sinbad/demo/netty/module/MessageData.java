package com.sinbad.demo.netty.module;

/**
 * 消息数据
 */
public class MessageData {

	private long curTime;

	private String uuid;

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
