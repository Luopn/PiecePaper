package com.example.piecepaper.activities;

import com.example.piecepaper.MainActivity;
import com.example.piecepaper.R;
import com.example.piecepaper.utils.ArgsRunnable;
import com.example.piecepaper.utils.CircleImageView;
import com.example.piecepaper.utils.HttpRequestRun;
import com.example.piecepaper.utils.MyImageLoader;
import com.example.piecepaper.utils.Ts;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class UserInfo extends Activity {
	Gson gson;
	CircleImageView iv;
	TextView tv_name;
	Ts ts;
	ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_userinfo);
		iv = (CircleImageView) findViewById(R.id.iv_userinfo_head);
		tv_name = (TextView) findViewById(R.id.user_name);
		ts = new Ts(getApplicationContext());

		Intent intent = getIntent();
		gson = new Gson();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		String userids = intent.getStringExtra("userid");
		ArgClass arg = new ArgClass();
		arg.userid = Integer.valueOf(userids);
		String argJson = gson.toJson(arg);
		Handler resultProHandler = new Handler();
		HttpRequestRun request = new HttpRequestRun(MainActivity.SERVER_Root + "/userinfo", argJson, resultProHandler,
				resultRun);
		Thread thrd = new Thread(request);
		thrd.start();
	}

	private ArgsRunnable resultRun = new ArgsRunnable() {
		public void run() {
			ArgClass arg = gson.fromJson(data, ArgClass.class);
			if (arg != null) {
				if (arg.resultCode == 1) {
					tv_name.setText(arg.userName);
					MyImageLoader mImageLoader = new MyImageLoader(imageLoader);
					mImageLoader.displayImage(arg.headPic, iv);
				} else {
					ts.showToast("拉取个人信息失败");
				}
			}
		};
	};

	class ArgClass {
		int userid, resultCode;
		String headPic, userName, regdate;
	}
}
