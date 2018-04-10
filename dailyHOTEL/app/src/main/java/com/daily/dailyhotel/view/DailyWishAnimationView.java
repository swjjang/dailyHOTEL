package com.daily.dailyhotel.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ScrollView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewWishAnimationDataBinding;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class DailyWishAnimationView extends ScrollView
{
    private DailyViewWishAnimationDataBinding mViewDataBinding;
    AnimatorSet mAnimatorSet;

    public DailyWishAnimationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyWishAnimationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyWishAnimationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_wish_animation_data, this, true);

    }

    public Observable<Boolean> addWishAnimation()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        if (mAnimatorSet != null && mAnimatorSet.isRunning() == true)
        {
            return null;
        }

        mViewDataBinding.wishTextView.setText(R.string.wishlist_detail_add_message);
        mViewDataBinding.wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_fill_l, 0, 0);
        mViewDataBinding.wishTextView.setBackgroundResource(R.drawable.shape_filloval_ccdb2453);

        return getAnimation(mViewDataBinding.wishTextView);
    }

    public Observable<Boolean> removeWishAnimation()
    {
        if (mViewDataBinding == null)
        {
            return null;
        }

        if (mAnimatorSet != null && mAnimatorSet.isRunning() == true)
        {
            return null;
        }

        mViewDataBinding.wishTextView.setText(R.string.wishlist_detail_delete_message);
        mViewDataBinding.wishTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_stroke_l, 0, 0);
        mViewDataBinding.wishTextView.setBackgroundResource(R.drawable.shape_filloval_a5000000);

        return getAnimation(mViewDataBinding.wishTextView);
    }

    private Observable<Boolean> getAnimation(View view)
    {
        if (view == null)
        {
            return null;
        }

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(view //
            , PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.2f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f, 1.0f) //
        );
        objectAnimator1.setInterpolator(new AccelerateInterpolator());
        objectAnimator1.setDuration(300);


        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(view //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 1.0f) //
        );
        objectAnimator2.setDuration(600);


        ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(view //
            , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f) //
            , PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f) //
        );
        objectAnimator3.setDuration(200);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                mAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (mAnimatorSet != null)
                        {
                            mAnimatorSet.removeAllListeners();
                            mAnimatorSet = null;
                        }

                        setVisibility(View.GONE);

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
    }
}
