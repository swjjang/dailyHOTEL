/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

public class ReviewScoreCardLayout extends ReviewCardLayout implements View.OnTouchListener
{
    private DailyEmoticonImageView[] mDailyEmoticonImageView;
    private DailyEmoticonImageView mSelectedEmoticonView;
    private int mReviewScore; // min : 1 ~ max : 5
    private OnScoreClickListener mOnScoreClickListener;
    //    private AnimatorSet mAnimatorSet;
    private DailyTextView mResultTextView;
    private View mEmoticonLayout;
    private boolean mUnabledDragEmotionLayout;

    public interface OnScoreClickListener
    {
        /**
         * @param reviewCardLayout
         * @param reviewScore      min : 1 ~ max : 5
         */
        void onClick(ReviewCardLayout reviewCardLayout, int reviewScore);
    }

    public ReviewScoreCardLayout(Context context, int position, ReviewScoreQuestion reviewScoreQuestion)
    {
        super(context, position);

        initLayout(context, reviewScoreQuestion);
    }

    private void initLayout(Context context, ReviewScoreQuestion reviewScoreQuestion)
    {
        // 카드 레이아웃
        setEnabled(false);
        setBackgroundResource(R.drawable.selector_review_cardlayout_enabled);

        int cardWidth = Util.getLCDWidth(context) - Util.dpToPx(context, 30);
        int cardHeight = Util.getRatioHeightType4x3(cardWidth);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardHeight);
        layoutParams.bottomMargin = Util.dpToPx(context, 15);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_score, this);

        View reviewScoreScrollView = (View) view.findViewById(R.id.reviewScoreScrollView);
        reviewScoreScrollView.setClickable(true);
        reviewScoreScrollView.setOnTouchListener(this);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);

        titleTextView.setText(reviewScoreQuestion.title);
        descriptionTextView.setText(reviewScoreQuestion.description);

        mEmoticonLayout = view.findViewById(R.id.emoticonLayout);

        mDailyEmoticonImageView = new DailyEmoticonImageView[5];

        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) mEmoticonLayout.findViewById(R.id.emoticonImageView0);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) mEmoticonLayout.findViewById(R.id.emoticonImageView1);
        mDailyEmoticonImageView[2] = (DailyEmoticonImageView) mEmoticonLayout.findViewById(R.id.emoticonImageView2);
        mDailyEmoticonImageView[3] = (DailyEmoticonImageView) mEmoticonLayout.findViewById(R.id.emoticonImageView3);
        mDailyEmoticonImageView[4] = (DailyEmoticonImageView) mEmoticonLayout.findViewById(R.id.emoticonImageView4);

        mDailyEmoticonImageView[0].setJSONData("Review_Animation.aep.comp-618-01_worst.kf.json");
        mDailyEmoticonImageView[1].setJSONData("Review_Animation.aep.comp-74-02_bad.kf.json");
        mDailyEmoticonImageView[2].setJSONData("Review_Animation.aep.comp-176-03_soso.kf.json");
        mDailyEmoticonImageView[3].setJSONData("Review_Animation.aep.comp-200-04_good.kf.json");
        mDailyEmoticonImageView[4].setJSONData("Review_Animation.aep.comp-230-05_awesome.kf.json");

        final int DP30 = Util.dpToPx(context, 30);
        final int DP30_DIV2 = DP30 / 2;

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.setPadding(DP30_DIV2, DP30, DP30_DIV2, 0);
        }

        mResultTextView = (DailyTextView) view.findViewById(R.id.resultTextView);

        //        RelativeLayout.LayoutParams resultLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //        resultLayoutParams.bottomMargin = cardHeight * 18 / 100;
        //        resultLayoutParams.topMargin = cardHeight * 12 / 100;
        //        resultLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //        resultLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //        mResultTextView.setLayoutParams(resultLayoutParams);
    }

    public void setOnScoreClickListener(OnScoreClickListener listener)
    {
        mOnScoreClickListener = listener;
    }

    public boolean isStartedAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return false;
        }

        if (isChecked() == true)
        {
            return mSelectedEmoticonView.isAnimationStart();
        } else
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                if (dailyEmoticonImageView.isAnimationStart() == true)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void startEmoticonAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return;
        }

        if (isChecked() == true)
        {
            mSelectedEmoticonView.startAnimation();
        } else
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.startAnimation();
            }
        }
    }

    public void stopEmoticonAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return;
        }

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.stopAnimation();
        }
    }

    public void pauseEmoticonAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return;
        }

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.pauseAnimation();
        }
    }

    public void resumeEmoticonAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return;
        }

        if (isChecked() == true)
        {
            mSelectedEmoticonView.resumeAnimation();
        } else
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.resumeAnimation();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (mUnabledDragEmotionLayout == true)
        {
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
            {
                mUnabledDragEmotionLayout = false;
            }

            return true;
        }

        float x = event.getX();
        float y = event.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                if (mEmoticonLayout.getTop() < y && mEmoticonLayout.getBottom() > y)
                {
                    mUnabledDragEmotionLayout = false;
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                } else
                {
                    mUnabledDragEmotionLayout = true;
                    return true;
                }

            case MotionEvent.ACTION_MOVE:
            {
                DailyEmoticonImageView selectedEmoticonView = null;

                for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
                {
                    if (mEmoticonLayout.getLeft() + dailyEmoticonImageView.getLeft() < x && mEmoticonLayout.getLeft() + dailyEmoticonImageView.getRight() > x)
                    {
                        selectedEmoticonView = dailyEmoticonImageView;
                        break;
                    }
                }

                if (selectedEmoticonView == null || mSelectedEmoticonView != null && mSelectedEmoticonView.getId() == selectedEmoticonView.getId())
                {
                    return true;
                }

                ValueAnimator scaleDownAnimator = null, scaleUpAnimator = null;

                if (mSelectedEmoticonView != null)
                {
                    ValueAnimator valueAnimator = (ValueAnimator) mSelectedEmoticonView.getTag();
                    if (valueAnimator != null)
                    {
                        valueAnimator.cancel();
                    }

                    scaleDownAnimator = getScaleDownAnimator(mSelectedEmoticonView);
                    mSelectedEmoticonView.setTag(scaleDownAnimator);
                    scaleDownAnimator.start();
                }

                checkedReviewEmoticon(selectedEmoticonView);

                mSelectedEmoticonView = selectedEmoticonView;

                if (selectedEmoticonView != null)
                {
                    ValueAnimator valueAnimator = (ValueAnimator) selectedEmoticonView.getTag();
                    if (valueAnimator != null)
                    {
                        valueAnimator.cancel();
                    }

                    scaleUpAnimator = getScaleUpAnimator(selectedEmoticonView);
                    selectedEmoticonView.setTag(scaleUpAnimator);
                    scaleUpAnimator.start();
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mUnabledDragEmotionLayout = false;
                view.getParent().requestDisallowInterceptTouchEvent(false);

                if (mOnScoreClickListener != null)
                {
                    mOnScoreClickListener.onClick(this, mReviewScore);
                }
                break;
        }

        return true;
    }

    @Override
    public boolean isChecked()
    {
        return mReviewScore > 0;
    }

    @Override
    public Object getReviewValue()
    {
        return mReviewScore;
    }

    private void checkedReviewEmoticon(View emoticonView)
    {
        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            if (dailyEmoticonImageView.getId() == emoticonView.getId())
            {
                dailyEmoticonImageView.setAlpha(1.0f);
            } else
            {
                dailyEmoticonImageView.setAlpha(0.5f);
            }
        }

        setEnabled(true);

        mResultTextView.setSelected(true);

        switch (emoticonView.getId())
        {
            case R.id.emoticonImageView0:
                mReviewScore = 1;
                mResultTextView.setText(R.string.label_review_score0);
                break;

            case R.id.emoticonImageView1:
                mReviewScore = 2;
                mResultTextView.setText(R.string.label_review_score1);
                break;

            case R.id.emoticonImageView2:
                mReviewScore = 3;
                mResultTextView.setText(R.string.label_review_score2);
                break;

            case R.id.emoticonImageView3:
                mReviewScore = 4;
                mResultTextView.setText(R.string.label_review_score3);
                break;

            case R.id.emoticonImageView4:
                mReviewScore = 5;
                mResultTextView.setText(R.string.label_review_score4);
                break;
        }

        DailyTextView titleTextView = (DailyTextView) findViewById(R.id.titleTextView);
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_circular_check, 0);
    }

    private ValueAnimator getScaleDownAnimator(final View view)
    {
        final float VALUE_DP7 = Util.dpToPx(mContext, 7);
        final float VALUE_DP15 = Util.dpToPx(mContext, 15);
        final int VALUE_DP8 = Util.dpToPx(mContext, 8);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(((LayoutParams) view.getLayoutParams()).leftMargin, -VALUE_DP8);
        valueAnimator.setDuration(200);

        final int VALUE_DP30 = Util.dpToPx(mContext, 30);
        final int VALUE_DP30_DIV2 = VALUE_DP30 / 2;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                try
                {
                    int value = (int) animation.getAnimatedValue();

                    float vectorValue = (VALUE_DP7 - value) / VALUE_DP15;

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = value;
                    layoutParams.rightMargin = value;
                    view.setLayoutParams(layoutParams);

                    int paddingValue1 = (int) (vectorValue * VALUE_DP30_DIV2);
                    int paddingValue2 = (int) (vectorValue * VALUE_DP30);

                    view.setPadding(paddingValue1, paddingValue2, paddingValue1, 0);
                    view.invalidate();
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            private boolean mIsCancel;

            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mIsCancel == true)
                {
                    return;
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = -VALUE_DP8;
                layoutParams.rightMargin = -VALUE_DP8;
                view.setLayoutParams(layoutParams);
                view.setPadding(VALUE_DP30_DIV2, VALUE_DP30, VALUE_DP30_DIV2, 0);
                view.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mIsCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        return valueAnimator;
    }

    private ValueAnimator getScaleUpAnimator(final View view)
    {
        final float VALUE_DP8 = Util.dpToPx(mContext, 8);
        final float VALUE_DP15 = Util.dpToPx(mContext, 15);
        final int VALUE_DP7 = Util.dpToPx(mContext, 7);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(((LayoutParams) view.getLayoutParams()).leftMargin, VALUE_DP7);
        valueAnimator.setDuration(200);

        final int VALUE_DP30 = Util.dpToPx(mContext, 30);
        final int VALUE_DP30_DIV2 = VALUE_DP30 / 2;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                try
                {
                    int value = (int) animation.getAnimatedValue();

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = value;
                    layoutParams.rightMargin = value;
                    view.setLayoutParams(layoutParams);

                    float vectorValue = (VALUE_DP8 + value) / VALUE_DP15;
                    int paddingValue1 = (int) ((1.0f - vectorValue) * VALUE_DP30_DIV2);
                    int paddingValue2 = (int) ((1.0f - vectorValue) * VALUE_DP30);

                    view.setPadding(paddingValue1, paddingValue2, paddingValue1, 0);
                    view.invalidate();
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            private boolean mIsCancel;

            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mIsCancel == true)
                {
                    return;
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = VALUE_DP7;
                layoutParams.rightMargin = VALUE_DP7;
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 0);
                view.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mIsCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        return valueAnimator;
    }
}
