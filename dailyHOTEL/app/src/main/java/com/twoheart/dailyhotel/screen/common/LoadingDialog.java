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
package com.twoheart.dailyhotel.screen.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class LoadingDialog
{
    private FrameLayout mProgressBarLayout;
    private BaseActivity mActivity;
    private Dialog mDialog;
    private ObjectAnimator mObjectAnimator;
    private int mRepeatCount;


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
                stopAnimation();

                mDialog.dismiss();
            }
        }
    };

    public LoadingDialog(BaseActivity activity)
    {
        mActivity = activity;

        mDialog = new Dialog(activity, R.style.TransDialog);

        final ImageView progressBarImageView = new ImageView(activity);
        progressBarImageView.setImageResource(R.drawable.loading_01);

        mObjectAnimator = ObjectAnimator.ofFloat(progressBarImageView, "alpha", 1.0f, 0.0f);
        mObjectAnimator.setDuration(500);
        mObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        mObjectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                progressBarImageView.setImageResource(R.drawable.loading_01);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {

            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
                switch (mRepeatCount++)
                {
                    case 0:
                        progressBarImageView.setImageResource(R.drawable.loading_02);
                    case 1:
                        break;

                    case 2:
                        progressBarImageView.setImageResource(R.drawable.loading_03);
                    case 3:
                        break;

                    case 4:
                        progressBarImageView.setImageResource(R.drawable.loading_04);
                    case 5:
                        break;

                    case 6:
                        progressBarImageView.setImageResource(R.drawable.loading_05);
                    case 7:
                        break;

                    case 8:
                        progressBarImageView.setImageResource(R.drawable.loading_06);
                    case 9:
                        break;

                    case 10:
                        progressBarImageView.setImageResource(R.drawable.loading_01);
                        break;

                    case 11:
                        mRepeatCount = 0;
                        break;

                }
            }
        });


        mProgressBarLayout = new FrameLayout(activity);
        mProgressBarLayout.setBackgroundResource(R.drawable.shape_fillrect_bffffff);
        mProgressBarLayout.addView(progressBarImageView);

        LayoutParams params = new LayoutParams(Util.dpToPx(activity, 90), Util.dpToPx(activity, 90));
        mDialog.addContentView(mProgressBarLayout, params);
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
            mProgressBarLayout.setVisibility(isShowProgress ? View.VISIBLE : View.INVISIBLE);

            try
            {
                mDialog.show();

                if (isShowProgress == true)
                {
                    startAnimation();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    /**
     * 이미 팝업은 뛰어져있으나 프로그래스바가 보이지 않을때..
     */
    public void showProgress()
    {
        if (mActivity == null || mActivity.isFinishing() == true)
        {
            return;
        }

        if (mDialog != null && mDialog.isShowing() == true)
        {
            if (mProgressBarLayout.getVisibility() != View.VISIBLE)
            {
                mProgressBarLayout.setVisibility(View.VISIBLE);

                startAnimation();
            }
        }
    }


    public void hide()
    {
        stopAnimation();

        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    public void close()
    {
        if (mDialog != null && mDialog.isShowing() == true)
        {
            stopAnimation();

            mDialog.dismiss();
        }

        mObjectAnimator = null;
        mDialog = null;
    }

    private void stopAnimation()
    {
        mRepeatCount = 0;
        mObjectAnimator.cancel();
    }

    private void startAnimation()
    {
        mObjectAnimator.setupStartValues();
        mObjectAnimator.start();
    }
}
