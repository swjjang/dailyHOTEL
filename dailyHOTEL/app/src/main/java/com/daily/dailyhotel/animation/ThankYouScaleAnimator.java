package com.daily.dailyhotel.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.util.VersionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by android_sam on 2017. 9. 18..
 */

public class ThankYouScaleAnimator
{
    private Context mContext;
    private View mRecommendGourmetView;

    Animator.AnimatorListener mAnimatorListener;

    public ThankYouScaleAnimator(Context context, View recommendGourmetView)
    {
        mContext = context;
        mRecommendGourmetView = recommendGourmetView;
    }

    public void setListener(Animator.AnimatorListener listener)
    {
        mAnimatorListener = listener;
    }

    public void start()
    {
        if (mContext == null)
        {
            return;
        }

        int animationDuration;

        if (VersionUtils.isOverAPI21() == true)
        {
            animationDuration = 200;
        } else
        {
            animationDuration = 200;
        }

        Collection animatorList = new ArrayList<>();

        if (mRecommendGourmetView != null)
        {
            ObjectAnimator recommendAnimator = getScaleAnimation(mContext, mRecommendGourmetView);
            recommendAnimator.setDuration(animationDuration);
            animatorList.add(recommendAnimator);
        }

        if (animatorList.size() == 0)
        {
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.addListener(mAnimatorListener);
        animatorSet.start();
    }

    private ObjectAnimator getScaleAnimation(Context context, final View view)
    {
        if (context == null || view == null)
        {
            return null;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view //
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
                if (view == null)
                {
                    return;
                }

                view.setVisibility(View.VISIBLE);
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
