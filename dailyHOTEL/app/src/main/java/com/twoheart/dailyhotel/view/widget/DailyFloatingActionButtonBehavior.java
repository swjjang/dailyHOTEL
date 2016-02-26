package com.twoheart.dailyhotel.view.widget;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.twoheart.dailyhotel.R;

public class DailyFloatingActionButtonBehavior extends CoordinatorLayout.Behavior
{
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public DailyFloatingActionButtonBehavior(Context context, AttributeSet attrs)
    {
        super();
    }

    public void hide(final View view)
    {
        if (view.getVisibility() == View.GONE || mIsAnimatingOut == true)
        {
            return;
        }

        if (Build.VERSION.SDK_INT >= 14)
        {
            ViewCompat.animate(view).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener()
            {
                public void onAnimationStart(View view)
                {
                    mIsAnimatingOut = true;
                }

                public void onAnimationCancel(View view)
                {
                    mIsAnimatingOut = false;
                }

                public void onAnimationEnd(View view)
                {
                    mIsAnimatingOut = false;
                    view.setVisibility(View.GONE);
                }
            }).start();
        } else
        {
            Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_fade_out);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(200L);
            anim.setAnimationListener(new Animation.AnimationListener()
            {
                public void onAnimationStart(Animation animation)
                {
                    mIsAnimatingOut = true;
                }

                public void onAnimationEnd(Animation animation)
                {
                    mIsAnimatingOut = false;
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(final Animation animation)
                {
                }
            });
            view.startAnimation(anim);
        }
    }

    public void show(View view)
    {
        if (view.getVisibility() == View.VISIBLE)
        {
            return;
        }

        view.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14)
        {
            ViewCompat.animate(view).scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start();
        } else
        {
            Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_fade_in);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            view.startAnimation(anim);
        }
    }
}
