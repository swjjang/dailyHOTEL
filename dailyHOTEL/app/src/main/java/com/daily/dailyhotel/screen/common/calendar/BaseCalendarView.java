package com.daily.dailyhotel.screen.common.calendar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCalendarDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class BaseCalendarView<T1 extends OnBaseEventListener, T2 extends ActivityCalendarDataBinding> extends BaseDialogView<T1, T2> implements View.OnClickListener
{
    private static final int ANIMATION_DELAY = 200;
    AnimatorSet mAnimatorSet;

    public BaseCalendarView(BaseActivity baseActivity, T1 listener)
    {
        super(baseActivity, listener);

        // VersionUtils.isOverAPI21()
        setStatusBarColor(getColor(R.color.black_a67));
    }

    @Override
    protected void setContentView(ActivityCalendarDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.calendarRecyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.exitView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().titleTextView.setText(title);
    }

    protected Observable<Boolean> showAnimation()
    {
        if (getViewDataBinding() == null || mAnimatorSet != null && mAnimatorSet.isStarted() == true)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                View animationLayout = getViewDataBinding().animationLayout;
                float y = animationLayout.getBottom();
                int height = animationLayout.getHeight();
                animationLayout.setTranslationY(ScreenUtils.dpToPx(getContext(), height));

                ObjectAnimator transAnimator = ObjectAnimator.ofFloat(animationLayout, "y", y, y - height);

                mAnimatorSet = new AnimatorSet();
                mAnimatorSet.play(transAnimator);
                mAnimatorSet.setDuration(ANIMATION_DELAY);
                mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        setVisible(true);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (mAnimatorSet != null)
                        {
                            mAnimatorSet.removeAllListeners();
                            mAnimatorSet = null;
                        }

                        observer.onNext(true);
                        observer.onComplete();
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

                mAnimatorSet.start();
            }
        };

        return observable.subscribeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<Boolean> hideAnimation()
    {
        if (getViewDataBinding() == null || mAnimatorSet != null && mAnimatorSet.isStarted() == true)
        {
            return null;
        }

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                View animationLayout = getViewDataBinding().animationLayout;
                float y = animationLayout.getTop();

                ObjectAnimator transAnimator = ObjectAnimator.ofFloat(animationLayout, "y", y, animationLayout.getBottom());
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(getViewDataBinding().getRoot(), "alpha", 1f, 0f);

                if (VersionUtils.isOverAPI21() == true)
                {
                    alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            if (animation == null)
                            {
                                return;
                            }

                            float value = (float) alphaAnimator.getAnimatedValue();
                            int color = (int) (0xab * value);

                            setStatusBarColor((color << 24) & 0xff000000);
                        }
                    });
                }

                mAnimatorSet = new AnimatorSet();
                mAnimatorSet.playTogether(transAnimator, alphaAnimator);
                mAnimatorSet.setDuration(ANIMATION_DELAY);
                mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (VersionUtils.isOverAPI21() == true)
                        {
                            alphaAnimator.removeAllUpdateListeners();
                        }

                        if (mAnimatorSet != null)
                        {
                            mAnimatorSet.removeAllListeners();
                            mAnimatorSet = null;
                        }

                        observer.onNext(true);
                        observer.onComplete();
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

                mAnimatorSet.start();
            }
        };

        return observable.subscribeOn(AndroidSchedulers.mainThread());
    }

    protected void setVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().animationLayout.setVisibility(visible == true ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setConfirmEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmView.setEnabled(enabled);
    }

    protected void setConfirmText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmView.setText(text);
    }
}
