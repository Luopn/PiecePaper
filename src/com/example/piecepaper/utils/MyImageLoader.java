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

	// ʹ��DisplayImageOptions.Builder()����DisplayImageOptions
	DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.loading) // ����ͼƬ�����ڼ���ʾ��ͼƬ
			.showImageForEmptyUri(R.drawable.loaderror) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
			.showImageOnFail(R.drawable.loaderror) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
			.cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
			.cacheOnDisc(true) // �������ص�ͼƬ�Ƿ񻺴���SD����
			.build(); // �������ù���DisplayImageOption����

	/**
	 * @param relaticePath
	 *            ͼƬ��Ե�ַ
	 * @param imageview
	 *            Ҫ����ͼƬ��imageview
	 */
	public void displayImage(String relaticePath, ImageView imageview) {

		imageLoader.displayImage(MainActivity.SERVER_Root + relaticePath, imageview, options,
				new AnimateFirstDisplayListener());
	}

	/**
	 * ͼƬ���ص�һ����ʾ������
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
				// �Ƿ��һ����ʾ
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// ͼƬ����Ч��
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
