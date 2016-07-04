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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
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
    private ImageView mProgressBarImageView;
    private BaseActivity mActivity;
    private Dialog mDialog;
    private AnimatorSet mAnimatorSet;
    private int mRepeatCount;
    private Drawable[] mDrawables;


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
                stopAnimation();
            }
        }
    };

    public LoadingDialog(BaseActivity activity)
    {
        mActivity = activity;
        mDialog = new Dialog(activity, R.style.TransDialog);
        //        mDialog = new Dialog(activity);

        mProgressBarImageView = new ImageView(activity);

        mDrawables = new Drawable[6];
        mDrawables[0] = activity.getResources().getDrawable(R.drawable.loading_01);
        mDrawables[1] = activity.getResources().getDrawable(R.drawable.loading_02);
        mDrawables[2] = activity.getResources().getDrawable(R.drawable.loading_03);
        mDrawables[3] = activity.getResources().getDrawable(R.drawable.loading_04);
        mDrawables[4] = activity.getResources().getDrawable(R.drawable.loading_05);
        mDrawables[5] = activity.getResources().getDrawable(R.drawable.loading_06);

        mAnimatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator01 = ObjectAnimator.ofFloat(mProgressBarImageView, "alpha", 0.0f, 1.0f);
        objectAnimator01.setDuration(100);

        ObjectAnimator objectAnimator02 = ObjectAnimator.ofFloat(mProgressBarImageView, "alpha", 1.0f, 1.0f);
        objectAnimator01.setDuration(400);

        ObjectAnimator objectAnimator03 = ObjectAnimator.ofFloat(mProgressBarImageView, "alpha", 1.0f, 0.0f);
        objectAnimator02.setDuration(100);

        mAnimatorSet.playSequentially(objectAnimator01, objectAnimator02, objectAnimator03);
        mAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mProgressBarImageView.setImageDrawable(mDrawables[mRepeatCount++]);

                if (mRepeatCount > 5)
                {
                    mRepeatCount = 0;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                startAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mProgressBarLayout = new FrameLayout(activity);
        mProgressBarLayout.setBackgroundResource(R.drawable.shape_fillrect_bbf000000);
        mProgressBarLayout.addView(mProgressBarImageView);

        LayoutParams params = new LayoutParams(Util.dpToPx(activity, 90), Util.dpToPx(activity, 90));
        mDialog.addContentView(mProgressBarLayout, params);
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
                if (isShowProgress == false)
                {
                    mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                } else
                {
                    mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }

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
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
    }

    public void close()
    {
        if (mDialog != null && mDialog.isShowing() == true)
        {
            stopAnimation();

            mDialog.dismiss();
        }

        mAnimatorSet = null;
        mDialog = null;
    }

    private void stopAnimation()
    {
        mRepeatCount = 0;
        mAnimatorSet.cancel();
    }

    private void startAnimation()
    {
        if (mAnimatorSet != null)
        {
            mAnimatorSet.start();
        }
    }
}
