package com.example.piecepaper.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.piecepaper.MainActivity;
import com.example.piecepaper.R;
import com.example.piecepaper.activities.UserInfo;
import com.example.piecepaper.utils.CircleImageView;
import com.example.piecepaper.utils.MyImageLoader;
import com.example.piecepaper.utils.Ts;
import com.example.piecepaper.utils.view.XListView;
import com.example.piecepaper.utils.view.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressLint("SimpleDateFormat")
public class NewMsgFragment extends Fragment {
	private XListView listview;
	private Ts ts;
	private String time;
	Handler mHandler = new Handler();
	private final String TAG = "HotMsgFragment";
	private RequestQueue mQueue;
	Gson gson = new Gson();
	protected ArrayList<MsgInfo> dataList;
	LayoutInflater mInflater;
	ImageLoader imageLoader = ImageLoader.getInstance();
	int maxVal;

	@Override
	public void onAttach(Activity activity) {
		ts = new Ts(activity);
		initTime();
		mQueue = Volley.newRequestQueue(getActivity());
		mQueue.start();// 启动队列线程
		// 发送服务器请求listview的数据。
		MsgArgClass reqArg = new MsgArgClass();// 构造请求参数对象
		reqArg.act = "hotmsg"; // 设置操作类型
		reqArg.min = 1;
		reqArg.max = 5;
		String requestBody = gson.toJson(reqArg);
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
				MainActivity.SERVER_Root + "/hotmsgshow", requestBody,
				listener, errorListener);
		mQueue.add(request);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_newmsg, null, false);
		listview = (XListView) view.findViewById(R.id.listview_new);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(mXListViewListener);
		mInflater = inflater;
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		maxVal = 5;
		return view;
	}

	// listview 更新数据
	private Listener<JSONObject> listener = new Listener<JSONObject>() {
		@Override
		// 服务器的Response数据通过这个方法返回；注意：这个方法是运行在UI线程的，不能在这个线程里做繁重的任务
		public void onResponse(JSONObject arg) {
			Log.d(TAG, "onResponse,result:" + arg.toString());
			ts.showToast("刷新成功");
			MsgArgClass data = gson.fromJson(arg.toString(), MsgArgClass.class);
			dataList = data.list;
			if (dataList.size() > 0) {
				listview.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged(); // 通知ListView更新数据绑定
			}
		}
	};

	ErrorListener errorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg) {
			Log.e(TAG, "volley request error; " + arg.getMessage());
			ts.showToast("请求失败");
		}
	};
	protected BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final MsgInfo info = dataList.get(position);
			View view = mInflater
					.inflate(R.layout.layout_newmsg_listview, null);
			ViewHolder tag = null;
			if (convertView == null) {
				ViewHolder holder = new ViewHolder();
				holder.head = (CircleImageView) view
						.findViewById(R.id.iv_newmsg_head);
				holder.username = (TextView) view
						.findViewById(R.id.tv_newmsg_username);
				holder.time = (TextView) view.findViewById(R.id.tv_newmsg_time);
				holder.msgpic = (ImageView) view
						.findViewById(R.id.iv_newmsg_msgpic);
				holder.content = (TextView) view
						.findViewById(R.id.tv_newmsg_msg);
				view.setTag(holder);
			} else {
				view = convertView;
			}
			tag = (ViewHolder) view.getTag();
			Log.d("图片路径", MainActivity.SERVER_Root + info.picPath);
			tag.content.setText(info.msgcontent);
			long date = Long.valueOf(info.regdate);
			tag.time.setText(new SimpleDateFormat("HH:mm")
					.format(new Date(date)));
			tag.username.setText(info.userName);

			tag.head.setTag(position);
			Log.d("settag时", position + "");
			tag.head.setImageResource(R.drawable.head_default);

			tag.head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), UserInfo.class);
					int userid = info.userid;
					intent.putExtra("userid", String.valueOf(userid));
					startActivity(intent);

				}
			});
			if (!TextUtils.isEmpty(info.picPath)
					&& !TextUtils.isEmpty(info.userPic)) {
				MyImageLoader mImageLoader = new MyImageLoader(imageLoader);
				mImageLoader.displayImage(info.picPath, tag.msgpic);
				mImageLoader.displayImage(info.userPic, tag.head);
			}
			return view;
		}
	};

	@Override
	public void onDetach() {
		mQueue.stop(); // 关闭请求队列
		super.onDetach();
	}

	private class ViewHolder {
		ImageView msgpic;
		CircleImageView head;
		TextView username, time, content;
	}

	private IXListViewListener mXListViewListener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mQueue = Volley.newRequestQueue(getActivity());
					mQueue.start();// 启动队列线程
					// 发送服务器请求listview的数据。
					MsgArgClass reqArg = new MsgArgClass();// 构造请求参数对象
					reqArg.min = 1;
					reqArg.max = maxVal;
					reqArg.act = "hotmsg"; // 设置操作类型
					String requestBody = gson.toJson(reqArg);
					JsonObjectRequest request = new JsonObjectRequest(
							Request.Method.POST, MainActivity.SERVER_Root
									+ "/hotmsgshow", requestBody, listener,
							errorListener);
					mQueue.add(request);
					onLoad();
				}
			}, 2000);
			mAdapter.notifyDataSetInvalidated();
		}

		@Override
		public void onLoadMore() {
			maxVal += 5;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mQueue = Volley.newRequestQueue(getActivity());
					mQueue.start();// 启动队列线程
					// 发送服务器请求listview的数据。
					MsgArgClass reqArg = new MsgArgClass();// 构造请求参数对象
					reqArg.act = "hotmsg"; // 设置操作类型
					reqArg.max = maxVal;
					String requestBody = gson.toJson(reqArg);
					JsonObjectRequest request = new JsonObjectRequest(
							Request.Method.POST, MainActivity.SERVER_Root
									+ "/hotmsgshow", requestBody, listener2,
							errorListener);
					mQueue.add(request);
					onLoad();
				}
			}, 2000);

		}
	};

	private void onLoad() {

		listview.stopRefresh();
		listview.stopLoadMore();
		listview.setRefreshTime(time);
		time = initTime();
	}

	private String initTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		time = format.format(new Date(System.currentTimeMillis()));
		return time;
	}

	private Listener<JSONObject> listener2 = new Listener<JSONObject>() {
		@Override
		// 服务器的Response数据通过这个方法返回；注意：这个方法是运行在UI线程的，不能在这个线程里做繁重的任务
		public void onResponse(JSONObject arg) {
			Log.d(TAG, "onResponse,result:" + arg.toString());
			ts.showToast("刷新成功");
			mAdapter.notifyDataSetChanged(); // 通知ListView更新数据绑定
		}
	};
}
