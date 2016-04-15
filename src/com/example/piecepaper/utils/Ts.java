package com.example.piecepaper.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast send message
 *
 */
public class Ts {
	Context context;
	public Ts(Context context){
		this.context = context;
	}
	
	public void showToast(String t){
		Toast.makeText(context, t, 
				Toast.LENGTH_SHORT).show();
	}
}
