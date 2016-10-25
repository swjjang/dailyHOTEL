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
public class FadeTransition extends Transition
{
    private static final String PROPNAME_BACKGROUND = "android:faderay:background";
    private static final String PROPNAME_ALPHA = "android:faderay:alpha";

    private float startAlpha;
    private float endAlpha;
    private TimeInterpolator timeInterpolator;

    public FadeTransition(final float startAlpha, final float endAlpha, final TimeInterpolator timeInterpolator)
    {
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
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
        startValues.view.setAlpha(1.0f);
        startValues.view.setAlpha(startAlpha);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(startValues.view, View.ALPHA, startAlpha, endAlpha);
        objectAnimator.setInterpolator(timeInterpolator);

        return objectAnimator;
    }
}