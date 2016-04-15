package com.example.piecepaper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.piecepaper.activities.LoginActivity;
import com.example.piecepaper.utils.ArgsRunnable;
import com.example.piecepaper.utils.HttpRequestRun;
import com.example.piecepaper.utils.PicUploadRun;
import com.example.piecepaper.utils.Ts;
import com.google.gson.Gson;

public class EditMsgActivity extends Activity {
	ImageButton back, picadd;
	PopupWindow popupWindow;
	Button sendMsg;
	private String filename;
	private File outputImage;
	private Uri imageUri;
	private final int PHOTO_REQUEST_CAMER = 1;
	private final int PHOTO_REQUEST_GALLERY = 2;
	private ImageView showImage;
	private Handler resultProHandler;
	private Gson gson = new Gson();
	private Ts ts = new Ts(EditMsgActivity.this);

	private EditText et_msgcontent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_editmsg);
		back = (ImageButton) findViewById(R.id.editmsg_back);
		picadd = (ImageButton) findViewById(R.id.addpic);
		sendMsg = (Button) findViewById(R.id.sendmsg);
		et_msgcontent = (EditText) findViewById(R.id.et_msgcontent);

		back.setOnClickListener(mOnClickListener);
		picadd.setOnClickListener(mOnClickListener);
		sendMsg.setOnClickListener(mOnClickListener);

		showImage = (ImageView) findViewById(R.id.picshow);
		resultProHandler = new Handler();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.editmsg_back:
				finish();
				break;
			case R.id.addpic:
				popupWindow = new PopupWindow(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				popupWindow.setFocusable(false);
				View contentView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.layout_popwindow, null);
				contentView.findViewById(R.id.popwindow_camera)
						.setOnClickListener(mPwindowSelect);
				contentView.findViewById(R.id.popwindow_gallery)
						.setOnClickListener(mPwindowSelect);
				contentView.findViewById(R.id.popwindow_cancel)
						.setOnClickListener(mPwindowSelect);

				popupWindow.setContentView(contentView);
				popupWindow.showAtLocation(v, Gravity.FILL, 0, 0);
				break;
			// �ϴ�ͼƬ������
			case R.id.sendmsg:
				if (outputImage != null) {
					PicUploadRun requestRun = new PicUploadRun(
							MainActivity.SERVER_Root + "/editmsg",
							outputImage.getAbsolutePath(), resultProHandler,
							picUploadRun);
					Log.d("", MainActivity.SERVER_Root + "/editmsg");
					new Thread(requestRun).start();
				}else{
					postProductInfo("");
				}
			}
		}

		OnClickListener mPwindowSelect = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				// �������ȡͼƬ
				case R.id.popwindow_camera:
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyyMMddHHmmss");
					Date date = new Date(System.currentTimeMillis());
					filename = format.format(date);
					// ����File�������ڴ洢���յ�ͼƬ SD����Ŀ¼,�洢��DCIM�ļ���
					File path = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					outputImage = new File(path, filename + ".jpg");
					try {
						if (outputImage.exists()) {
							outputImage.delete();
						}
						outputImage.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// ��File����ת��ΪUri�������������
					imageUri = Uri.fromFile(outputImage);
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // ָ��ͼƬ�����ַ
					startActivityForResult(intent, PHOTO_REQUEST_CAMER); // ��������
					popupWindow.dismiss();
					break;
				// ������ȡͼƬ
				case R.id.popwindow_gallery:
					Intent intent2 = new Intent(Intent.ACTION_PICK, null);
					intent2.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
					popupWindow.dismiss();
					break;
				case R.id.popwindow_cancel:
					popupWindow.dismiss();
					break;
				}
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			Toast.makeText(EditMsgActivity.this,
					"ActivityResult resultCode error", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		switch (requestCode) {
		case PHOTO_REQUEST_CAMER:
			try {
				// ͼƬ������Bitmap����
				Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(imageUri));
				Toast.makeText(EditMsgActivity.this, imageUri.toString(),
						Toast.LENGTH_SHORT).show();
				showImage.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case PHOTO_REQUEST_GALLERY:
			Uri dataUri = data.getData();//
			Log.d("--->>", "onActivityResult,GALLERY:" + dataUri);
			String[] proj = { MediaStore.Images.Media.DATA }; // MediaStore.Images.Media.DATA,Ϊ�ļ���ʵ��·��
			Cursor cur = getContentResolver().query(dataUri, proj, null, null,
					null);
			cur.moveToFirst();
			// img_path ���ͼƬ��ʵ��·��
			String img_path = cur.getString(cur
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
			outputImage = new File(img_path);
			Bitmap bitmap = BitmapFactory.decodeFile(img_path);
			showImage.setImageBitmap(bitmap);
			break;
		}
	}

	private ArgsRunnable picUploadRun = new ArgsRunnable() {
		public void run() {
			ArgClass arg = gson.fromJson(data, ArgClass.class);
			if (arg.act.equalsIgnoreCase("msgpic")) {
				if (arg.resultCode == 1) {
					ts.showToast("ͼƬ�ϴ��ɹ�");
					// �����ϴ���Ʒ���ı���Ϣ
					postProductInfo(arg.picPath);
				} else {
					ts.showToast("ͼƬ�ϴ�ʧ��");
				}
			}
		};
	};

	protected void postProductInfo(String picPath) {
		String msg = et_msgcontent.getText().toString();
		ArgClass arg = new ArgClass();
		if (msg.isEmpty()) {
			ts.showToast("����������");
		}
		arg.act = "msginfo";
		arg.msgcontent = msg;
		arg.picPath = picPath;
		arg.userid = LoginActivity.userId;
		String jsonArg = gson.toJson(arg); // ��arg����ת����json��ʽ���ַ���

		HttpRequestRun requestRun = new HttpRequestRun(MainActivity.SERVER_Root
				+ "/editmsginfo", jsonArg, resultProHandler, resultRun);
		Thread rqThrd = new Thread(requestRun);
		rqThrd.start();
	}

	ArgsRunnable resultRun = new ArgsRunnable() {
		@Override
		public void run() {
			ArgClass arg = gson.fromJson(data, ArgClass.class);
			if (arg != null && arg.act.equalsIgnoreCase("msgsend")) {
				if (arg.resultCode == 1) {
					ts.showToast("���ݷ��ͳɹ�");
					finish();
				} else {
					ts.showToast("���ݷ���ʧ��");
				}
			}
		}
	};

}
