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
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressLint("SimpleDateFormat")
public class HotMsgFragment extends Fragment {
	private ListView listview;
	private Ts ts;
	public Handler bgThrdHandler;
	HandlerThread mHandlerThread = new HandlerThread("HotMsgFragment_bg_thrd");

	private final String TAG = "HotMsgFragment";
	private RequestQueue mQueue;
	Gson gson = new Gson();
	protected ArrayList<MsgInfo> dataList;
	LayoutInflater mInflater;
	ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onAttach(Activity activity) {
		ts = new Ts(activity);
		mHandlerThread.start();
		bgThrdHandler = new Handler(mHandlerThread.getLooper());

		mQueue = Volley.newRequestQueue(getActivity());
		mQueue.start();// ���������߳�

		// ���ͷ���������listview�����ݡ�
		MsgArgClass reqArg = new MsgArgClass();// ���������������
		reqArg.act = "hotmsg"; // ���ò�������
		String requestBody = gson.toJson(reqArg);
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, MainActivity.SERVER_Root + "/hotmsgshow",
				requestBody, listener, errorListener);
		mQueue.add(request);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_newmsg, null, false);
		listview = (ListView) view.findViewById(R.id.listview_new);
		mInflater = inflater;
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		return view;
	}

	// listview ��������
	private Listener<JSONObject> listener = new Listener<JSONObject>() {
		@Override
		// ��������Response����ͨ������������أ�ע�⣺���������������UI�̵߳ģ�����������߳��������ص�����
		public void onResponse(JSONObject arg) {
			Log.d(TAG, "onResponse,result:" + arg.toString());
			ts.showToast("ˢ�³ɹ�");
			MsgArgClass data = gson.fromJson(arg.toString(), MsgArgClass.class);
			dataList = data.list;
			if (dataList.size() > 0) {
				listview.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged(); // ֪ͨListView�������ݰ�
			}
		}
	};

	ErrorListener errorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg) {
			Log.e(TAG, "volley request error; " + arg.getMessage());
			ts.showToast("����ʧ��");
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
			View view = mInflater.inflate(R.layout.layout_newmsg_listview, null);
			ViewHolder tag = null;
			if (convertView == null) {
				ViewHolder holder = new ViewHolder();
				holder.head = (CircleImageView) view.findViewById(R.id.iv_newmsg_head);
				holder.username = (TextView) view.findViewById(R.id.tv_newmsg_username);
				holder.time = (TextView) view.findViewById(R.id.tv_newmsg_time);
				holder.msgpic = (ImageView) view.findViewById(R.id.iv_newmsg_msgpic);
				holder.content = (TextView) view.findViewById(R.id.tv_newmsg_msg);
				view.setTag(holder);
			} else {
				view = convertView;
			}
			tag = (ViewHolder) view.getTag();
			Log.d("ͼƬ·��", MainActivity.SERVER_Root + info.picPath);
			tag.content.setText(info.msgcontent);
			long date = Long.valueOf(info.regdate);
			tag.time.setText(new SimpleDateFormat("HH:mm").format(new Date(date)));
			tag.username.setText(info.userName);

			tag.head.setTag(position);
			Log.d("settagʱ", position + "");
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
			if (!TextUtils.isEmpty(info.picPath) && !TextUtils.isEmpty(info.userPic)) {
				MyImageLoader mImageLoader = new MyImageLoader(imageLoader);
				mImageLoader.displayImage(info.picPath, tag.msgpic);
				mImageLoader.displayImage(info.userPic, tag.head);
			}
			return view;
		}
	};

	@Override
	public void onDetach() {
		mQueue.stop(); // �ر��������
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		mHandlerThread.quit(); // ������̨�߳�
		super.onDestroy();
	}

	private class ViewHolder {
		ImageView msgpic;
		CircleImageView head;
		TextView username, time, content;
	}
}
