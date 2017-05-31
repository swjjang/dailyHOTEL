package com.daily.base.util;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.daily.base.BaseActivity;
import com.daily.base.R;

public class DailyLock
{
    private boolean mIsLock = false;
    private ProgressBarDialog mProgressBarDialog;

    public DailyLock(BaseActivity activity)
    {
        mProgressBarDialog = new ProgressBarDialog(activity);
    }

    public boolean isLock()
    {
        synchronized (this)
        {
            return mIsLock;
        }
    }

    /**
     * 락을 건다.
     *
     * @return lock 걸기전의 lock 상태
     */
    public boolean lock()
    {
        synchronized (this)
        {
            if (mIsLock == true)
            {
                return true;
            } else
            {
                mIsLock = true;
                return false;
            }
        }
    }

    /**
     * 락을 해제한다.
     */
    public void unLock()
    {
        synchronized (this)
        {
            mIsLock = false;

            mProgressBarDialog.hide();
        }
    }

    /**
     * 스크린 락을 건다.
     */
    public boolean screenLock(boolean showProgress)
    {
        mProgressBarDialog.show(showProgress);

        return lock();
    }

    /**
     * 스크린 락을 해제한다.
     */
    public void screenUnLock()
    {
        mProgressBarDialog.hide();

        unLock();
    }

    /**
     * 메모리를 해지한다. Activity 종료시
     */
    public void clear()
    {
        mProgressBarDialog.close();
        unLock();
    }

    private class ProgressBarDialog
    {
        private ProgressBar mProgressBar;
        private Dialog mDialog;
        private BaseActivity mActivity;

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

        ProgressBarDialog(BaseActivity activity)
        {
            mActivity = activity;

            mDialog = new Dialog(activity, R.style.TransBaseDialog);
            mProgressBar = new ProgressBar(activity);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDialog.addContentView(mProgressBar, params);
            mDialog.setCancelable(false);

            mDialog.setOnCancelListener(dialog ->
            {
                hide();

                mActivity.onBackPressed();
            });

            mDialog.setOnKeyListener((dialog, keyCode, event) ->
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled())
                {
                    mActivity.onBackPressed();
                }

                return false;
            });
        }

        void show(boolean showProgress)
        {
            mHandler.removeMessages(0);

            if (mActivity == null || mActivity.isFinishing() == true || mDialog == null)
            {
                return;
            }

            if (mDialog.isShowing() == true)
            {
                if (showProgress == true && mProgressBar.getVisibility() == View.INVISIBLE)
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (showProgress == false && mProgressBar.getVisibility() == View.VISIBLE)
                {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            } else
            {
                mProgressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);

                try
                {
                    mDialog.show();
                } catch (Exception e)
                {
                }
            }
        }

        void hide()
        {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 300);
        }

        void close()
        {
            if (mDialog != null && mDialog.isShowing() == true)
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }
    }
}
