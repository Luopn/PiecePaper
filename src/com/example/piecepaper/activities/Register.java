package com.example.piecepaper.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.piecepaper.MainActivity;
import com.example.piecepaper.R;
import com.example.piecepaper.utils.ArgsRunnable;
import com.example.piecepaper.utils.HttpRequestRun;
import com.example.piecepaper.utils.Ts;
import com.google.gson.Gson;

public class Register extends Activity {
	private EditText et_name, et_passcode, et_passcode_cer;
	private Button bt_register;
	private ImageButton bt_back;
	private Gson gson;
	protected Handler resultProHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		et_name = (EditText) findViewById(R.id.et_name);
		et_passcode = (EditText) findViewById(R.id.et_passcode);
		et_passcode = (EditText) findViewById(R.id.et_passcode);
		et_passcode_cer = (EditText) findViewById(R.id.et_passcode_cer);
		bt_register = (Button) findViewById(R.id.bt_register);
		bt_back = (ImageButton) findViewById(R.id.bt_regsiter_back);
		bt_register.setOnClickListener(mOnClickListener);
		bt_back.setOnClickListener(mOnClickListener);
		gson = new Gson();
		resultProHandler = new Handler();
	}

	private OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// for registering
			case R.id.bt_register:
				String name = et_name.getText().toString().trim();
				String passcode = et_passcode.getText().toString().trim();
				String passcode_cer = et_passcode_cer.getText().toString()
						.trim();
				ArgClass arg = new ArgClass();
				if (!name.isEmpty() && !passcode.isEmpty()
						&& !passcode_cer.isEmpty()) {
					if (passcode.equals(passcode_cer)) {
						arg.act = "register";
						arg.name = name;
						arg.passcode = passcode;

						String args = gson.toJson(arg);
						Thread thrd = new Thread(new HttpRequestRun(
								MainActivity.SERVER_Root + "/register", args,
								resultProHandler, resultRun));
						thrd.start();
					} else {
						ts.showToast("两次输入的密码不一致");
					}
				} else
					ts.showToast("name or passcode can't be null");
				break;

			case R.id.bt_regsiter_back:
				finish();
			}

		}
	};
	protected Ts ts = new Ts(Register.this);
	ArgsRunnable resultRun = new ArgsRunnable() {
		@Override
		public void run() {
			ArgClass arg = gson.fromJson(data, ArgClass.class);
			if (arg != null) {
				if (arg.act.equalsIgnoreCase("register")) {
					if (arg.resultCode == 1) {
						ts.showToast("恭喜！注册成功");
						finish();
					} else {
						ts.showToast("注册失败，用户名重复");
					}
				}
			}
		}
	};
}
