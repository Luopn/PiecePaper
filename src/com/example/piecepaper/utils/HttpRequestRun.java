package com.example.piecepaper.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;

public class HttpRequestRun implements Runnable {
	String url;
	String args;
	ArgsRunnable resultRun;
	private Handler mResultProHandler;

	/**
	 * resultProRun用来处理http请求的结果
	 */
	public HttpRequestRun(String url, String argJson, Handler resultProHandler,
			ArgsRunnable resultRun) {
		this.url = url;
		this.args = argJson;
		mResultProHandler = resultProHandler;
		this.resultRun = resultRun;
	}

	@Override
	public void run() {
		URL servletUrl;
		InputStream in = null;
		HttpURLConnection conn = null;
		try {
			servletUrl = new URL(url);
			conn = (HttpURLConnection) servletUrl.openConnection();
			/*
			 * 2、设置http请求头
			 */
			conn.setRequestProperty("accept", "*/*");// 说明客户端可接收的数据类类型
			conn.setRequestProperty("connection", "Keep-Alive");// http连接的超时或保持连接
			conn.setRequestProperty("user-agent", // 标明客户端（浏览器）类型等信息
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept-Charset", "utf-8"); // 接收文本的编码格式
			conn.setRequestProperty("Content-Type", // 说明客户商端发送的数据类型及编码格式
					"application/json; charset=utf-8");

			/*
			 * 设置POST请求模式
			 */
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setChunkedStreamingMode(0);
			conn.setDoInput(true);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream(), "utf-8"));

			out.write(args); // 输出流写数据时并没有直接把数据发给服务器，而是把数据放到本地缓存里
			out.flush();

			// 读取服务器资源，首先会向服务器发送一个Http请求,这时就会把请求参数传给服务器
			in = conn.getInputStream();
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int count = 0;
			while ((count = in.read(bytes)) > 0) // 读取服务器数据时，使用字节流读取比较理想
			{
				bos.write(bytes, 0, count);
			}
			byte[] strByte = bos.toByteArray();
			String jSonString = null;
			if (bos.size() > 0) {
				jSonString = new String(strByte, "UTF-8");// 把读到的字节全部转换成字符串
			}
			bos.close();
			/*
			 * 处理结果
			 */
			resultRun.setData(jSonString);
			mResultProHandler.post(resultRun);
			out.close();

		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}
