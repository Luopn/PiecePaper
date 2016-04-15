package com.example.piecepaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.piecepaper.activities.LoginActivity;
import com.example.piecepaper.fragment.HotMsgFragment;
import com.example.piecepaper.fragment.NewMsgFragment;
import com.example.piecepaper.utils.Ts;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity {
	public static final String SERVER_Root = "http://10.22.0.146:8081/DemoServer";
	private RadioGroup group;
	private FragmentManager mFragmentManager;
	Ts ts = new Ts(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViewById(R.id.to_editmsg).setOnClickListener(mOnClickListener);
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
				.replace(R.id.sub_container, new NewMsgFragment()).commit();
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.LEFT);
		menu.setMenu(R.layout.layout_menu);

		group = (RadioGroup) findViewById(R.id.radiogroup_main);
		group.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.bt_hot:
				mFragmentManager.beginTransaction()
						.replace(R.id.sub_container, new HotMsgFragment())
						.commit();
				break;
			case R.id.bt_new:
				mFragmentManager.beginTransaction()
						.replace(R.id.sub_container, new NewMsgFragment())
						.commit();
				break;

			}

		}
	};
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (LoginActivity.Logined == true) {
				Intent intent = new Intent(getApplicationContext(),
						EditMsgActivity.class);
				startActivity(intent);
			} else {
				ts.showToast("请先登录");
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
			}

		}
	};
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	           ts.showToast("再按一次退出程序");                             
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
