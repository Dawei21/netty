package com.sinbad.demo.netty.buffer;

import java.nio.IntBuffer;
import java.util.Random;

/**
 * @author sinbad on 2021/5/14
 **/
public class BasicBuffer {


	public static void main(String[] args) {

		Random random = new Random();
		//创建一个buffer 可以存放 5 个int 数据
		IntBuffer intBuffer = IntBuffer.allocate(5);

		for (int i = 0; i < intBuffer.capacity() + 1; i++) {
			int num = i *   random.nextInt(100);
			intBuffer.put(num);
			System.out.println("Put num:" + num);
		}


		System.out.println("------");

		//从random中读取数据， 需要将buffer 转换，读写切换
		intBuffer.flip();


		while (intBuffer.hasRemaining()) {
			int num = intBuffer.get();
			System.out.println("Get num:" + num);
		}
	}
}
