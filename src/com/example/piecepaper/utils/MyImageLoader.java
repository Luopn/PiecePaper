package com.example.piecepaper.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.example.piecepaper.MainActivity;
import com.example.piecepaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class MyImageLoader {
	ImageLoader imageLoader;

	public MyImageLoader(ImageLoader imageLoader) {
		super();
		this.imageLoader = imageLoader;
	}

	// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
	DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.loading) // 设置图片下载期间显示的图片
			.showImageForEmptyUri(R.drawable.loaderror) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.loaderror) // 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
			.build(); // 创建配置过得DisplayImageOption对象

	/**
	 * @param relaticePath
	 *            图片相对地址
	 * @param imageview
	 *            要加载图片的imageview
	 */
	public void displayImage(String relaticePath, ImageView imageview) {

		imageLoader.displayImage(MainActivity.SERVER_Root + relaticePath, imageview, options,
				new AnimateFirstDisplayListener());
	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
