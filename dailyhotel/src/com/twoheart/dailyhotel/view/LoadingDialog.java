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
package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class LoadingDialog
{
	private BaseActivity mActivity;
	private Dialog mDialog;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (mActivity == null || mActivity.isFinishing() == true)
			{
				return;
			}

			if (mDialog != null && mDialog.isShowing())
			{
				mDialog.dismiss();
			}
		}
	};

	public LoadingDialog(BaseActivity activity)
	{
		mActivity = activity;

		mDialog = new Dialog(activity, R.style.TransDialog);
		ProgressBar pb = new ProgressBar(activity);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mDialog.addContentView(pb, params);
		mDialog.setCancelable(false);

		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				hide();

				mActivity.onProgressBackPressed();
			}
		});

		mDialog.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled())
				{
					mActivity.onProgressBackPressed();
				}

				return false;
			}
		});
	}

	public boolean isVisible()
	{
		if (mDialog == null)
		{
			return false;
		}

		return mDialog.isShowing();
	}

	public void show()
	{
		if (mActivity == null || mActivity.isFinishing() == true)
		{
			return;
		}

		mHandler.removeMessages(0);

		if (mDialog != null && mDialog.isShowing() == false)
		{
			try
			{
				mDialog.show();
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}
	}

	public void hide()
	{
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 500);
	}

	public void close()
	{
		if (mDialog != null && mDialog.isShowing() == true)
		{
			mDialog.dismiss();
		}

		mDialog = null;
	}
}
