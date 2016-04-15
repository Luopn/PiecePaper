package com.example.piecepaper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.piecepaper.MainActivity;
import com.example.piecepaper.R;
import com.example.piecepaper.utils.ArgsRunnable;
import com.example.piecepaper.utils.HttpRequestRun;
import com.example.piecepaper.utils.Ts;
import com.google.gson.Gson;

public class LoginActivity extends Activity {
	public static int userId = 0;
	public static boolean Logined = false;
	EditText et_name, et_passcode;
	Button bt_login, bt_toregis;
	Ts ts;
	Gson gson;
	private Handler resultProHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		et_name = (EditText) findViewById(R.id.et_name);
		et_passcode = (EditText) findViewById(R.id.et_passcode);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_toregis = (Button) findViewById(R.id.btn_toregis);
		bt_login.setOnClickListener(mOnClickListener);
		bt_toregis.setOnClickListener(mOnClickListener);
		resultProHandler = new Handler();
		ts = new Ts(this);
		gson = new Gson();
	}
	private OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// for logining
			case R.id.bt_login:
				String name = et_name.getText().toString().trim();
				String passcode = et_passcode.getText().toString().trim();
				ArgClass arg = new ArgClass();
				if (!name.isEmpty() && !passcode.isEmpty()) {
					arg.act = "login";
					arg.name = name;
					arg.passcode = passcode;
					String args = gson.toJson(arg);
					Thread thrd = new Thread(new HttpRequestRun(MainActivity.SERVER_Root+"/login",
							args, resultProHandler, resultRun));
					thrd.start();
				} else
					ts.showToast("name or passcode can't be null");
				break;

			case R.id.btn_toregis:
				Intent intent = new Intent(getApplicationContext(), Register.class);
				startActivity(intent);
				break;
			}

		}
	};
	ArgsRunnable resultRun = new ArgsRunnable() {
		@Override
		public void run() {
			ArgClass arg = gson.fromJson(data, ArgClass.class);
			if (arg != null&&arg.act!=null) {
				if (arg.act.equalsIgnoreCase("login")) {
					if (arg.resultCode == 1) {
						ts.showToast("µÇÂ¼³É¹¦");
						Logined = true;
						userId = arg.userid;
						Intent intent = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(intent);
						finish();
					} else {
						ts.showToast("ÕËºÅ»òÃÜÂë´íÎó");
					}
				}
			}else{
				ts.showToast("µÇÂ¼Òì³£");
			}

		}
	};
}
