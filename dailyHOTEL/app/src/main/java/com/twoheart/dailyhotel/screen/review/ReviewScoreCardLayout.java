/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

public class ReviewScoreCardLayout extends ReviewCardLayout implements View.OnClickListener
{
    private DailyEmoticonImageView[] mDailyEmoticonImageView;
    private DailyEmoticonImageView mSelectedEmoticonView;
    private int mReviewScore; // min : 1 ~ max : 5
    private OnScoreClickListener mOnScoreClickListener;
    private AnimatorSet mAnimatorSet;

    public interface OnScoreClickListener
    {
        /**
         * @param reviewCardLayout
         * @param reviewScore      min : 1 ~ max : 5
         */
        void onClick(ReviewCardLayout reviewCardLayout, int reviewScore);
    }

    public ReviewScoreCardLayout(Context context, ReviewScoreQuestion reviewScoreQuestion)
    {
        super(context);

        initLayout(context, reviewScoreQuestion);
    }

    private void initLayout(Context context, ReviewScoreQuestion reviewScoreQuestion)
    {
        // 카드 레이아웃
        setBackgroundResource(R.drawable.selector_review_cardlayout);

        int cardWidth = Util.getLCDWidth(context) - Util.dpToPx(context, 30);
        int cardHeight = Util.getRatioHeightType4x3(cardWidth);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardHeight);
        layoutParams.bottomMargin = Util.dpToPx(context, 15);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_score, this);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);

        titleTextView.setText(reviewScoreQuestion.title);
        descriptionTextView.setText(reviewScoreQuestion.description);

        View emoticonView = view.findViewById(R.id.emoticonView);

        mDailyEmoticonImageView = new DailyEmoticonImageView[5];

        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView0);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView1);
        mDailyEmoticonImageView[2] = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView2);
        mDailyEmoticonImageView[3] = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView3);
        mDailyEmoticonImageView[4] = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView4);

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
            dailyEmoticonImageView.setOnClickListener(this);
        }
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
    public void onClick(View view)
    {
        if (mSelectedEmoticonView != null && mSelectedEmoticonView.getId() == view.getId())
        {
            return;
        }

        if (mAnimatorSet != null && (mAnimatorSet.isStarted() == true || mAnimatorSet.isRunning()))
        {
            return;
        }

        mAnimatorSet = new AnimatorSet();
        ValueAnimator scaleDownAnimator = null, scaleUpAnimator = null;

        if (mSelectedEmoticonView != null)
        {
            scaleDownAnimator = getScaleDownAnimator(mSelectedEmoticonView);
        }

        checkedReviewEmoticon(view);

        mSelectedEmoticonView = (DailyEmoticonImageView) view;

        if (view != null)
        {
            scaleUpAnimator = getScaleUpAnimator(view);
        }

        if (scaleDownAnimator != null && scaleUpAnimator != null)
        {
            mAnimatorSet.playTogether(scaleDownAnimator, scaleUpAnimator);
        } else if (scaleUpAnimator != null)
        {
            mAnimatorSet.playTogether(scaleUpAnimator);
        }

        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorSet.start();

        // 순서가 중요 위의 체크가 끝나야한다.
        if (mOnScoreClickListener != null)
        {
            mOnScoreClickListener.onClick(this, mReviewScore);
        }
    }

    @Override
    public boolean isChecked()
    {
        return mReviewScore > 0;
    }

    private void checkedReviewEmoticon(View emoticonView)
    {
        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            if (dailyEmoticonImageView.getId() == emoticonView.getId())
            {
                dailyEmoticonImageView.setAlpha(1.0f);
                dailyEmoticonImageView.startAnimation();
            } else
            {
                dailyEmoticonImageView.setAlpha(0.5f);
                dailyEmoticonImageView.stopAnimation();
            }
        }

        setSelected(true);

        TextView resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView.setSelected(true);

        switch (emoticonView.getId())
        {
            case R.id.emoticonImageView0:
                mReviewScore = 1;
                resultTextView.setText(R.string.label_review_score0);
                break;

            case R.id.emoticonImageView1:
                mReviewScore = 2;
                resultTextView.setText(R.string.label_review_score1);
                break;

            case R.id.emoticonImageView2:
                mReviewScore = 3;
                resultTextView.setText(R.string.label_review_score2);
                break;

            case R.id.emoticonImageView3:
                mReviewScore = 4;
                resultTextView.setText(R.string.label_review_score3);
                break;

            case R.id.emoticonImageView4:
                mReviewScore = 5;
                resultTextView.setText(R.string.label_review_score4);
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

        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) VALUE_DP7, -VALUE_DP8);
        valueAnimator.setDuration(200);

        final int VALUE_DP30 = Util.dpToPx(mContext, 30);
        final int VALUE_DP30_DIV2 = VALUE_DP30 / 2;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
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
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = -VALUE_DP8;
                layoutParams.rightMargin = -VALUE_DP8;
                view.setLayoutParams(layoutParams);
                view.setPadding(VALUE_DP30_DIV2, VALUE_DP30, VALUE_DP30_DIV2, 0);
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

        return valueAnimator;
    }

    private ValueAnimator getScaleUpAnimator(final View view)
    {
        final float VALUE_DP8 = Util.dpToPx(mContext, 8);
        final float VALUE_DP15 = Util.dpToPx(mContext, 15);
        final int VALUE_DP7 = Util.dpToPx(mContext, 7);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(Util.dpToPx(mContext, -8), VALUE_DP7);
        valueAnimator.setDuration(200);

        final int VALUE_DP30 = Util.dpToPx(mContext, 30);
        final int VALUE_DP30_DIV2 = VALUE_DP30 / 2;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
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
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = VALUE_DP7;
                layoutParams.rightMargin = VALUE_DP7;
                view.setLayoutParams(layoutParams);
                view.setPadding(0, 0, 0, 0);
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

        return valueAnimator;
    }
}
