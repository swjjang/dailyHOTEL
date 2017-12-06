package com.daily.dailyhotel.screen.home.stay.outbound.calendar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCalendarDataBinding;
import com.twoheart.dailyhotel.databinding.ViewCalendarDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public abstract class PlaceCalendarView<T1 extends PlaceCalendarView.OnEventListener, T2 extends ActivityCalendarDataBinding> extends BaseDialogView<T1, T2> implements View.OnClickListener
{
    private static final int ANIMATION_DELAY = 200;
    AnimatorSet mAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public PlaceCalendarView(BaseActivity baseActivity, T1 listener)
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

    Observable<Boolean> showAnimation()
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
                        setVisibility(true);
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

        return observable;
    }

    Observable<Boolean> hideAnimation()
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

        return observable;
    }

    void setVisibility(boolean visibility)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().animationLayout.setVisibility(visibility == true ? View.VISIBLE : View.INVISIBLE);
    }

    void setConfirmEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmView.setEnabled(enabled);
    }

    void setConfirmText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmView.setText(text);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void setStatusBarColor(int color)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
