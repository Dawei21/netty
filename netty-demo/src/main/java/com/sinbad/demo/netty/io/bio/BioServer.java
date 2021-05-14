package com.sinbad.demo.netty.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sinbad on 2021/5/14
 **/
public class BioServer {


	//客户端 通过telnet 运行

	public static void main(String[] args) throws IOException {
		ExecutorService newFixedThreadPool = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(10), runnable -> new Thread(runnable, "t_pl_pool_" + runnable.hashCode()), new ThreadPoolExecutor.DiscardOldestPolicy());

		ServerSocket serverSocket = new ServerSocket(9000);


		System.out.println("服务器Running 。。。");

		while (true) {
			final Socket socket = serverSocket.accept();
			System.out.println("新链接建立");
			newFixedThreadPool.execute(() -> handler(socket));
		}

	}

	private static void handler(final Socket socket) {

		System.out.println("Run thread: " + Thread.currentThread().getName());
		byte[] bytes = new byte[2048];
		try (
				InputStream inputStream = socket.getInputStream()) {
			while (true) {
				int read = inputStream.read(bytes);
				if (read != -1) {
					String message = new String(bytes, 0, read);
					System.out.println(message);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
