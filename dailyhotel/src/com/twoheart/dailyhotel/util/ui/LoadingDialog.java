/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * LoadingDialog
 * 
 * Activity 전체를 핸들하는 로딩 다이얼로그 창이다. 로딩 작업을 수행하는
 * 동안 로딩 다이얼로그 창을 띄우며 취소시 Activity의 onBackPressed
 * 메서드도 같이 수행된다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.twoheart.dailyhotel.R;

public class LoadingDialog
{
	private Dialog mDialog;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (mDialog != null && mDialog.isShowing())
			{
				mDialog.dismiss();
			}
		}
	};

	public LoadingDialog(final BaseActivity activity)
	{
		mDialog = new Dialog(activity, R.style.TransDialog);
		ProgressBar pb = new ProgressBar(activity);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mDialog.addContentView(pb, params);
		mDialog.setCancelable(true);
		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				hide();
				activity.onBackPressed();
			}
		});
	}

	public boolean isVisible()
	{
		return mDialog.isShowing();
	}

	public void show()
	{
		mHandler.removeMessages(0);

		if (!mDialog.isShowing())
			mDialog.show();
	}

	public void hide()
	{
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 500);

		//		if (mDialog.isShowing())
		//			mDialog.dismiss();
	}

	public void dismiss()
	{
		if (mDialog.isShowing() == true)
		{
			mDialog.dismiss();
		}
	}
}
