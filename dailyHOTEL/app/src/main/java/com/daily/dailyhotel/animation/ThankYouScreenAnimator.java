package com.daily.dailyhotel.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;

public class ThankYouScreenAnimator
{
    private Context mContext;
    private View mCheckView;
    private View mInformationView;

    private Animator.AnimatorListener mAnimatorListener;

    public ThankYouScreenAnimator(Context context, View checkView, View informationView)
    {
        mContext = context;
        mCheckView = checkView;
        mInformationView = informationView;
    }

    public void setListener(Animator.AnimatorListener listener)
    {
        mAnimatorListener = listener;
    }

    public void start()
    {
        if (mContext == null || mCheckView == null || mInformationView == null)
        {
            return;
        }

        int animatorSetStartDelay;
        int receiptLayoutAnimatorDuration;
        int confirmImageAnimatorStartDelay;
        int confirmImageAnimatorDuration;

        if (VersionUtils.isOverAPI21() == true)
        {
            animatorSetStartDelay = 400;
            receiptLayoutAnimatorDuration = 300;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
        } else
        {
            animatorSetStartDelay = 600;
            receiptLayoutAnimatorDuration = 400;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(animatorSetStartDelay);

        ObjectAnimator informationAnimator = getInformationAnimation(mContext);
        informationAnimator.setDuration(receiptLayoutAnimatorDuration);

        ObjectAnimator checkAnimator = getCheckAnimation(mContext);
        checkAnimator.setDuration(confirmImageAnimatorDuration);
        checkAnimator.setStartDelay(confirmImageAnimatorStartDelay);

        animatorSet.addListener(mAnimatorListener);
        animatorSet.playTogether(informationAnimator, checkAnimator);
        animatorSet.start();
    }

    private ObjectAnimator getCheckAnimation(Context context)
    {
        if (context == null)
        {
            return null;
        }

        final float startScaleY = 2.3f;
        final float endScaleY = 1.0f;

        final ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mCheckView //
            , PropertyValuesHolder.ofFloat("scaleX", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("scaleY", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f) //
        );

        objectAnimator.setInterpolator(new OvershootInterpolator(1.6f));
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mCheckView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();

                mCheckView.setScaleX(endScaleY);
                mCheckView.setScaleY(endScaleY);
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

        return objectAnimator;
    }

    private ObjectAnimator getInformationAnimation(Context context)
    {
        if (context == null)
        {
            return null;
        }

        final float startY = 0f - ScreenUtils.getScreenHeight(context);
        final float endY = 0.0f;

        final ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mInformationView //
            , PropertyValuesHolder.ofFloat("translationY", startY, endY)//
        );

        objectAnimator.setInterpolator(new OvershootInterpolator(0.82f));
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mInformationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();

                mInformationView.setTranslationY(endY);
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

        return objectAnimator;
    }
}
