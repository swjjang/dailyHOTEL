package com.daily.dailyhotel.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.util.VersionUtils;

public class StayThankYouScreenAnimator extends ThankYouScreenAnimator
{
    View mStampView;

    public StayThankYouScreenAnimator(Context context, View checkView, View informationView, View stampView)
    {
        super(context, checkView, informationView);

        mStampView = stampView;
    }

    @Override
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
        int stampLayoutAnimatorStartDelay;
        int stampLayoutAnimatorDuration;

        if (VersionUtils.isOverAPI21() == true)
        {
            animatorSetStartDelay = 400;
            receiptLayoutAnimatorDuration = 300;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        } else
        {
            animatorSetStartDelay = 600;
            receiptLayoutAnimatorDuration = 400;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(animatorSetStartDelay);

        ObjectAnimator informationAnimator = getInformationAnimation(mContext);
        informationAnimator.setDuration(receiptLayoutAnimatorDuration);

        ObjectAnimator checkAnimator = getCheckAnimation(mContext);
        checkAnimator.setDuration(confirmImageAnimatorDuration);
        checkAnimator.setStartDelay(confirmImageAnimatorStartDelay);

        if (mStampView != null)
        {
            ObjectAnimator stampAnimator = getStampAnimation(mContext);
            stampAnimator.setDuration(stampLayoutAnimatorDuration);
            stampAnimator.setStartDelay(stampLayoutAnimatorStartDelay);

            animatorSet.playTogether(informationAnimator, checkAnimator, stampAnimator);
        } else
        {
            animatorSet.playTogether(informationAnimator, checkAnimator);
        }

        animatorSet.addListener(mAnimatorListener);
        animatorSet.start();
    }

    private ObjectAnimator getStampAnimation(Context context)
    {
        if (context == null)
        {
            return null;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mStampView //
            , PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f) //
        );

        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mStampView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();
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
