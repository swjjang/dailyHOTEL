package com.twoheart.dailyhotel.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(21)
public class AlphaTransition extends Transition
{
    private View mView;
    private float mStartAlpha, mEndAlpha;
    private TimeInterpolator mTimeInterpolator;

    public AlphaTransition(final float startAlpha, final float endAlpha, final TimeInterpolator timeInterpolator)
    {
        mStartAlpha = startAlpha;
        mEndAlpha = endAlpha;
        mTimeInterpolator = timeInterpolator;
    }

    public AlphaTransition(final View view, final float startAlpha, final float endAlpha, final TimeInterpolator timeInterpolator)
    {
        mView = view;
        mStartAlpha = startAlpha;
        mEndAlpha = endAlpha;
        mTimeInterpolator = timeInterpolator;
    }

    public AlphaTransition(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    private void captureValues(final TransitionValues transitionValues)
    {
    }

    @Override
    public void captureStartValues(final TransitionValues transitionValues)
    {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(final TransitionValues transitionValues)
    {
        captureValues(transitionValues);
    }

    @SuppressLint("NewApi")
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, final TransitionValues startValues, final TransitionValues endValues)
    {
        ObjectAnimator objectAnimator;

        if (mView == null)
        {
            objectAnimator = ObjectAnimator.ofFloat(startValues.view, View.ALPHA, mStartAlpha, mEndAlpha);
            objectAnimator.setInterpolator(mTimeInterpolator);
        } else
        {
            objectAnimator = ObjectAnimator.ofFloat(mView, View.ALPHA, mStartAlpha, mEndAlpha);
            objectAnimator.setInterpolator(mTimeInterpolator);
        }

        return objectAnimator;
    }
}