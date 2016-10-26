package com.twoheart.dailyhotel.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

@TargetApi(21)
public class TextTransition extends Transition
{
    private static final String PROPNAME_TEXT_SIZE = "dailyTransition:textSize";

    private int mStartColor, mEndColor;
    private float mStartSize, mEndSize;
    private TimeInterpolator mTimeInterpolator;

    public TextTransition(final int startColor, final int endColor, final float startSize, final float endSize, final TimeInterpolator timeInterpolator)
    {
        mStartColor = startColor;
        mEndColor = endColor;
        mStartSize = startSize;
        mEndSize = endSize;
        mTimeInterpolator = timeInterpolator;
    }

    public TextTransition(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    private void captureValues(final TransitionValues transitionValues)
    {
        if (transitionValues.view instanceof TextView)
        {
            transitionValues.values.put(PROPNAME_TEXT_SIZE, ((TextView) transitionValues.view).getTextSize());
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

        // 원래 아래 코드를 사용하려고 했지만 값이 제대로 전달되지 않아서 다른 입력 받는 것으로 수정
        //        float startTextSize = (Float) startValues.values.get(PROPNAME_TEXT_SIZE);
        //        float endTextSize = (Float) endValues.values.get(PROPNAME_TEXT_SIZE);

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator colorValueAnimator = ValueAnimator.ofArgb(mStartColor, mEndColor);
        colorValueAnimator.setInterpolator(mTimeInterpolator);
        colorValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                textView.setTextColor((int) valueAnimator.getAnimatedValue());
            }
        });

        ValueAnimator sizeValueAnimator = ValueAnimator.ofFloat(mStartSize, mEndSize);
        sizeValueAnimator.setInterpolator(mTimeInterpolator);
        sizeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (Float) valueAnimator.getAnimatedValue());
            }
        });

        animatorSet.playTogether(colorValueAnimator, sizeValueAnimator);

        return animatorSet;
    }
}