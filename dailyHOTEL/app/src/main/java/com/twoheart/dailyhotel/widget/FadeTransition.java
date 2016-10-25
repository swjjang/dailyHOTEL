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
import android.widget.TextView;

@TargetApi(21)
public class FadeTransition extends Transition
{
    private static final String PROPNAME_BACKGROUND = "android:faderay:background";
    private static final String PROPNAME_TEXT_COLOR = "android:faderay:textColor";
    private static final String PROPNAME_ALPHA = "android:faderay:alpha";

    private int startColor;
    private int endColor;
    private TimeInterpolator timeInterpolator;

    public FadeTransition(final int startColor, final int endColor, final TimeInterpolator timeInterpolator)
    {
        this.startColor = startColor;
        this.endColor = endColor;
        this.timeInterpolator = timeInterpolator;
    }

    public FadeTransition(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    private void captureValues(final TransitionValues transitionValues)
    {
        transitionValues.values.put(PROPNAME_BACKGROUND, transitionValues.view.getBackground());
        transitionValues.values.put(PROPNAME_ALPHA, transitionValues.view.getAlpha());

        if (transitionValues.view instanceof TextView)
        {
            transitionValues.values.put(PROPNAME_TEXT_COLOR, ((TextView) transitionValues.view).getCurrentTextColor());
        }
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
        final TextView textView = (TextView) startValues.view;

        ValueAnimator valueAnimator = ValueAnimator.ofArgb(startColor, endColor);
        valueAnimator.setInterpolator(timeInterpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                textView.setTextColor((int) valueAnimator.getAnimatedFraction());
            }
        });

        return valueAnimator;
    }
}