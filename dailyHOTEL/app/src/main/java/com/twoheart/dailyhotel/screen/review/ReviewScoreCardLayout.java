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
    private View mSelectedEmoticonView;
    private int mReviewScore;
    private OnEmoticonClickListener mOnEmoticonClickListener;
    private AnimatorSet mAnimatorSet;

    public interface OnEmoticonClickListener
    {
        void onClick(View reviewScoreCardLayout, int reviewScore);
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

        mDailyEmoticonImageView[0].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[1].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[2].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[3].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[4].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.setScaleX(0.55f);
            dailyEmoticonImageView.setScaleY(0.55f);

            dailyEmoticonImageView.setTranslationY(Util.dpToPx(mContext, 15));
            dailyEmoticonImageView.setOnClickListener(this);
            dailyEmoticonImageView.startAnimation();
        }
    }

    public void setOnEmoticonClickListener(OnEmoticonClickListener listener)
    {
        mOnEmoticonClickListener = listener;
    }

    public void startEmoticonAnimation()
    {
        if (mDailyEmoticonImageView == null)
        {
            return;
        }

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.startAnimation();
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
            dailyEmoticonImageView.setOnClickListener(null);
            dailyEmoticonImageView.stopAnimation();
        }

        mDailyEmoticonImageView = null;
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

        mSelectedEmoticonView = view;

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
        if (mOnEmoticonClickListener != null)
        {
            mOnEmoticonClickListener.onClick(this, mReviewScore);
        }
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

        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) VALUE_DP7, Util.dpToPx(mContext, -8));
        valueAnimator.setDuration(200);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                float vectorValue = (VALUE_DP7 - value) / VALUE_DP15;
                float scaleValue = 1.0f - 0.45f * vectorValue;

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = value;
                layoutParams.rightMargin = value;

                view.setLayoutParams(layoutParams);
                view.setScaleX(scaleValue);
                view.setScaleY(scaleValue);

                float transValue = VALUE_DP15 * vectorValue;
                view.setTranslationY(transValue);
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
                view.setScaleX(0.55f);
                view.setScaleY(0.55f);
                view.setTranslationY(VALUE_DP15);
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

        ValueAnimator valueAnimator = ValueAnimator.ofInt(Util.dpToPx(mContext, -8), Util.dpToPx(mContext, 7));
        valueAnimator.setDuration(200);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();

                float vectorValue = (float) (VALUE_DP8 + value) / VALUE_DP15;
                float scaleValue = 0.55f + 0.45f * vectorValue;

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                layoutParams.leftMargin = value;
                layoutParams.rightMargin = value;

                view.setLayoutParams(layoutParams);
                view.setScaleX(scaleValue);
                view.setScaleY(scaleValue);

                float transValue = VALUE_DP15 * (1.0f - vectorValue);
                view.setTranslationY(transValue);
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
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setTranslationY(0);
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
