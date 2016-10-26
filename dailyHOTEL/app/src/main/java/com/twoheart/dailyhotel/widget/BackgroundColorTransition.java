package com.twoheart.dailyhotel.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.ViewGroup;

@TargetApi(21)
public class BackgroundColorTransition extends Transition
{
    private int mStartColor, mEndColor;
    private TimeInterpolator mTimeInterpolator;

    public BackgroundColorTransition(final int startColor, final int endColor, final TimeInterpolator timeInterpolator)
    {
        mStartColor = startColor;
        mEndColor = endColor;
        mTimeInterpolator = timeInterpolator;
    }

    public BackgroundColorTransition(final Context context, final AttributeSet attrs)
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
        ValueAnimator colorValueAnimator = ValueAnimator.ofArgb(mStartColor, mEndColor);
        colorValueAnimator.setInterpolator(mTimeInterpolator);
        colorValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                startValues.view.setBackgroundColor((int) valueAnimator.getAnimatedValue());
            }
        });

        return colorValueAnimator;
    }
}