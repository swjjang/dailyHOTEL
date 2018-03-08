package com.daily.dailyhotel.screen.home.search;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewSearchGourmetFilterDataBinding;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;

public class SearchGourmetFilterView extends ConstraintLayout implements View.OnClickListener
{
    private ViewSearchGourmetFilterDataBinding mViewDataBinding;

    private OnGourmetFilterListener mFilterListener;

    public interface OnGourmetFilterListener
    {
        void onSuggestClick();

        void onCalendarClick();

        void onSearchClick();
    }

    public SearchGourmetFilterView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public SearchGourmetFilterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public SearchGourmetFilterView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_search_gourmet_filter_data, this, true);

        mViewDataBinding.suggestBackgroundView.setOnClickListener(this);
        mViewDataBinding.calendarBackgroundView.setOnClickListener(this);
        mViewDataBinding.searchTextView.setOnClickListener(this);
    }

    public void setOnFilterListener(OnGourmetFilterListener listener)
    {
        mFilterListener = listener;
    }

    public void setSuggestText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.suggestTextView.setText(text);
    }

    public void setCalendarText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.calendarTextView.setText(text);
    }

    public void setSearchEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.searchTextView.setEnabled(enabled);
    }

    public Completable getSuggestTextViewAnimation()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mViewDataBinding.suggestBackgroundView, View.ALPHA, 1.0f, 0.5f, 1.0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mViewDataBinding.suggestTextView, View.ALPHA, 1.0f, 0.5f, 1.0f);

        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(mViewDataBinding.suggestBackgroundView, View.SCALE_X, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(mViewDataBinding.suggestBackgroundView, View.SCALE_Y, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(mViewDataBinding.suggestTextView, View.SCALE_X, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(mViewDataBinding.suggestTextView, View.SCALE_Y, 1.0f, 0.97f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimator1, objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);

        return new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
            {
                animatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        animatorSet.removeAllListeners();
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

                animatorSet.start();
            }
        };
    }

    @Override
    public void onClick(View v)
    {
        if (mFilterListener == null)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.suggestBackgroundView:
                mFilterListener.onSuggestClick();
                break;

            case R.id.calendarBackgroundView:
                mFilterListener.onCalendarClick();
                break;

            case R.id.searchTextView:
                mFilterListener.onSearchClick();
                break;
        }
    }
}
