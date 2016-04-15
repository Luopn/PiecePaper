package com.example.piecepaper.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.util.Log;

public class PicUploadRun implements Runnable {
	private static final String TAG = "SendHttpArgRequestRun";
	String url;
	String filePath; // 本地要上传的图片路径
	ArgsRunnable resultRun;
	private Handler mResultProHandler;

	/**
	 * resultProRun用来处理http请求的结果
	 */
	public PicUploadRun(String url, String filePath, Handler resultProHandler,
			ArgsRunnable resultRun) {
		this.url = url;
		this.filePath = filePath;
		mResultProHandler = resultProHandler;
		this.resultRun = resultRun;
	}

	@Override
	public void run() {
		URL servletUrl;
		InputStream in = null;
		HttpURLConnection conn = null;
		FileInputStream fileIn = null;
		File imgFile = new File(filePath);
		if (!imgFile.exists() || imgFile.length() < 1) {
			Log.e(TAG, "上传的文件" + imgFile + " 不存在！");
			return;
		}

		try {
			servletUrl = new URL(url);
			conn = (HttpURLConnection) servletUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");// 说明客户端可接收的数据类型
			conn.setRequestProperty("connection", "Keep-Alive");// http连接的超时或保持连接
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept-Charset", "utf-8"); // 接收文本的编码格式
			conn.setRequestProperty("Content-Type", // 说明客户商端发送的数据类型及编码格式
					"application/image; charset=utf-8");

			/*
			 * 2.置POST请求模式
			 */
			conn.setRequestMethod("POST");
			conn.setDoOutput(true); // 说明这个连接是要求进行数据输出的，它会自动把请求方式设置为POST模式
			conn.setChunkedStreamingMode(0);
			conn.setDoInput(true);

			BufferedOutputStream out = new BufferedOutputStream(
					conn.getOutputStream());

			// 写入文件�???
			String fileName = imgFile.getName();
			byte[] nameBytes = fileName.getBytes("UTF-8");
			short fileNameLenght = (short) nameBytes.length;
			byte[] lenBytes = new byte[2];
			lenBytes[0] = (byte) fileNameLenght; // 把short变量的右边一字节赋给lenBytes[0]
			lenBytes[1] = (byte) (fileNameLenght >> 8);// 把short变量的左边一字节赋给lenBytes[1]

			Log.d(TAG,
					"fileNameLenght:" + fileNameLenght + ","
							+ Integer.toHexString(lenBytes[0]) + ","
							+ Integer.toHexString(lenBytes[1]));

			out.write(lenBytes); // 先发送表示文件名长度的两个字
			out.write(nameBytes);// 再发送文件名的字节数

			// 文件输入流
			fileIn = new FileInputStream(imgFile);
			byte[] buffer = new byte[1024];
			int readed = 0;
			while ((readed = fileIn.read(buffer)) > 0) {
				out.write(buffer, 0, readed); // 输出出流写数据时并没有直接把数据发给服务器，而是把数据放到本地缓存
			}
			out.flush();
			fileIn.close();
			/*
			 * 4、读取HTTP应答主体，发送请求；
			 */
			// 读取服务器资源，首先会向服务器发送一个Http请求,这时就会把请求参数传给服务器。
			in = conn.getInputStream();
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int count = 0;
			while ((count = in.read(bytes)) > 0) // 读取服务器数据时，使用字节流读取比较理想。
			{
				bos.write(bytes, 0, count);
			}
			byte[] strByte = bos.toByteArray();
			String jSonString = null;
			if (bos.size() > 0) {
				jSonString = new String(strByte, "UTF-8");// 把读到的字节全部转换成字符
			}
			bos.close();
			Log.d(TAG, "response : " + jSonString);

			/*
			 * 处理结果
			 */
			resultRun.setData(jSonString);
			mResultProHandler.post(resultRun);
			out.close();

		} catch (Exception e) {
			System.out.println("发送POST请求出现异常" + e);
			e.printStackTrace();
		} finally {
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
