package com.twoheart.dailyhotel.place.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.lang.ref.WeakReference;

public abstract class PlaceCurationActivity extends BaseActivity implements View.OnClickListener
{
    private static final int HANDLE_MESSAGE_RESULT = 1;
    private static final int HANDLE_MESSAGE_DELAYTIME = 750;

    private static final int ANIMATION_DEALY = 200;

    private TextView mResultCountView;
    private View mConfirmView;

    private Handler mHandler;

    protected View mAnimationLayout; // 애니메이션 되는 뷰
    private View mDisableLayout; // 전체 화면을 덮는 뷰
    private View mBackgroundView; // 뒷배경

    private ANIMATION_STATUS mAnimationStatus = ANIMATION_STATUS.HIDE_END;
    private ANIMATION_STATE mAnimationState = ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;

    protected abstract void initContentLayout(ViewGroup contentLayout);

    protected abstract void onComplete();

    protected abstract void onCancel();

    protected abstract void onReset();

    protected abstract void updateResultMessage();

    protected void initLayout()
    {
        setContentView(R.layout.activity_curation);

        mHandler = new UpdateHandler(this);

        mResultCountView = (TextView) findViewById(R.id.resultCountView);
        mConfirmView = findViewById(R.id.confirmView);
        setConfirmOnClickListener(this);

        ScrollView contentScrollView = (ScrollView) findViewById(R.id.contentScrollView);
        EdgeEffectColor.setEdgeGlowColor(contentScrollView, getResources().getColor(R.color.over_scroll_edge));

        View exitView = findViewById(R.id.exitView);
        exitView.setOnClickListener(this);

        View resetCurationView = findViewById(R.id.resetCurationView);
        resetCurationView.setOnClickListener(this);

        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);

        mAnimationLayout = findViewById(R.id.animationLayout);
        mDisableLayout = findViewById(R.id.disableLayout);
        mBackgroundView = (View) exitView.getParent();

        initContentLayout(contentLayout);
    }

    protected void setResultMessage(String text)
    {
        if (mResultCountView == null)
        {
            return;
        }

        mResultCountView.setText(text);
    }

    protected void setConfirmEnable(boolean enabled)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setEnabled(enabled);
    }

    protected void setConfirmOnClickListener(View.OnClickListener listener)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setOnClickListener(listener);
    }

    protected void requestUpdateResult()
    {
        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_RESULT);
    }

    protected void requestUpdateResultDelayed()
    {
        setConfirmOnClickListener(null);

        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        hideAnimation();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                onComplete();
                break;

            case R.id.closeView:
            case R.id.exitView:
                onCancel();
                break;

            case R.id.resetCurationView:
                onReset();
                break;
        }
    }

    protected DailyTextView getGridLayoutItemView(String text, int resId, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(this);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getResources().getColorStateList(R.drawable.selector_curation_textcolor));
        dailyTextView.setText(text);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            dailyTextView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 15));
        } else
        {
            dailyTextView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    protected void setTouchEnabled(boolean enabled)
    {
        if (enabled == true)
        {

            mDisableLayout.setVisibility(View.GONE);
            mDisableLayout.setOnClickListener(null);
        } else
        {
            mDisableLayout.setVisibility(View.VISIBLE);
            mDisableLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }

    protected void showAnimation()
    {
        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.SHOW)
        {
            return;
        }

        if (Util.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getBottom();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mAnimationLayout.getHeight();

            mAnimationLayout.setTranslationY(Util.dpToPx(this, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, y - height);
            mObjectAnimator.setDuration(ANIMATION_DEALY);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mAnimationLayout.getVisibility() != View.VISIBLE)
                    {
                        mAnimationLayout.setVisibility(View.VISIBLE);
                    }

                    setTouchEnabled(false);

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                        mAnimationState = ANIMATION_STATE.END;
                    }

                    setTouchEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;

                    setTouchEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();

//            showAnimationFadeOut();
        } else
        {
            if (mAnimationLayout != null && mAnimationLayout.getVisibility() != View.VISIBLE)
            {
                mAnimationLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = ANIMATION_STATUS.SHOW_END;
                mAnimationState = ANIMATION_STATE.END;
            }
        }
    }

    protected void hideAnimation()
    {
        if (mAnimationState == ANIMATION_STATE.START && mAnimationStatus == ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (Util.isOverAPI12() == true)
        {
            final float y = mAnimationLayout.getTop();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mAnimationLayout, "y", y, mAnimationLayout.getBottom());
            mObjectAnimator.setDuration(ANIMATION_DEALY);

            mObjectAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {

                    mAnimationState = ANIMATION_STATE.START;
                    mAnimationStatus = ANIMATION_STATUS.HIDE;

                    setTouchEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = ANIMATION_STATUS.HIDE_END;
                        mAnimationState = ANIMATION_STATE.END;

                        mBackgroundView.setVisibility(View.GONE);

                        finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = ANIMATION_STATE.CANCEL;

                    mBackgroundView.setVisibility(View.GONE);

                    finish();
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }
            });

            mObjectAnimator.start();

            showAnimationFadeIn();
        } else
        {
            mAnimationStatus = ANIMATION_STATUS.HIDE_END;
            mAnimationState = ANIMATION_STATE.END;

            finish();
        }
    }

    /**
     * 점점 밝아짐.
     */
    private void showAnimationFadeIn()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(ANIMATION_DEALY);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mBackgroundView.startAnimation(mAlphaAnimation);
    }

    /**
     * 점점 어두워짐.
     */
    private void showAnimationFadeOut()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(ANIMATION_DEALY);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mBackgroundView.startAnimation(mAlphaAnimation);
    }

    private static class UpdateHandler extends Handler
    {
        private final WeakReference<PlaceCurationActivity> mWeakReference;

        public UpdateHandler(PlaceCurationActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PlaceCurationActivity placeCurationActivity = mWeakReference.get();

            if (placeCurationActivity == null)
            {
                return;
            }

            switch (msg.what)
            {
                case HANDLE_MESSAGE_RESULT:
                    placeCurationActivity.updateResultMessage();
                    break;
            }

        }
    }
}