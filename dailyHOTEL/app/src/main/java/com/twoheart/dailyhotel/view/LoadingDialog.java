/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * LoadingDialog
 * <p>
 * Activity 전체를 핸들하는 로딩 다이얼로그 창이다. 로딩 작업을 수행하는
 * 동안 로딩 다이얼로그 창을 띄우며 취소시 Activity의 onBackPressed
 * 메서드도 같이 수행된다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;

public class LoadingDialog
{
    private ProgressBar mProgressBar;
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
        mProgressBar = new ProgressBar(activity);
        mProgressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.probressbar_default), PorterDuff.Mode.SRC_IN);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mDialog.addContentView(mProgressBar, params);
        mDialog.setCancelable(false);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

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

    public void setCancelable(boolean falg)
    {
        mDialog.setCancelable(false);
    }

    public boolean isVisible()
    {
        return mDialog != null && mDialog.isShowing();

    }

    public void show(boolean isShowProgress)
    {
        if (mActivity == null || mActivity.isFinishing() == true)
        {
            return;
        }

        mHandler.removeMessages(0);

        if (mDialog != null && mDialog.isShowing() == false)
        {
            mProgressBar.setVisibility(isShowProgress ? View.VISIBLE : View.INVISIBLE);

            try
            {
                mDialog.show();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    public void showProgress()
    {
        if (mActivity == null || mActivity.isFinishing() == true)
        {
            return;
        }

        if (mDialog != null && mDialog.isShowing() == true)
        {
            mProgressBar.setVisibility(View.VISIBLE);
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
