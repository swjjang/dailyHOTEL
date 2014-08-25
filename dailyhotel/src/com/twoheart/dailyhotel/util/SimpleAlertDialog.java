package com.twoheart.dailyhotel.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class SimpleAlertDialog {
	
	public static Builder build(Context context, String title, String msg, String positive,
			String negative, OnClickListener posListener, OnClickListener negaListener) {
		if (negaListener == null) negaListener = getDefaultListener();
		
		return SimpleAlertDialog.build(context, title, msg, positive, posListener)
				.setNegativeButton(negative, negaListener);
	}
	
	public static Builder build(Context context, String title, String msg, String positive, OnClickListener posListener) {
		android.util.Log.e("msg",msg);
		Builder alert = SimpleAlertDialog.build(context, msg, positive, posListener);
		if (title != null && !title.isEmpty())  alert.setTitle(title);
		return alert; 
	}
	
	public static Builder build(Context context, String msg, String positive, OnClickListener posListener) {
		if (posListener == null) posListener = getDefaultListener();
		
		return new AlertDialog.Builder(context)
		.setPositiveButton(positive, posListener)
		.setMessage(msg);
	}
	
	private static OnClickListener getDefaultListener() {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
	}
	
}
