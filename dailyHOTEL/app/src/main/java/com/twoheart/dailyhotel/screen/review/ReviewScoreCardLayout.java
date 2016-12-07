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
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewAnswerValue;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

public class ReviewScoreCardLayout extends ReviewCardLayout implements View.OnClickListener
{
    private DailyEmoticonImageView[] mDailyEmoticonImageView;
    private View mSelectedEmoticonView, mSelectedView;

    public ReviewScoreCardLayout(Context context)
    {
        super(context);
    }

    private void initLayout(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_score, this);

        int cardWidth = Util.getLCDWidth(context) - Util.dpToPx(context, 30);
        int cardHeight = Util.getRatioHeightType4x3(cardWidth);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cardWidth, cardHeight);
        layoutParams.bottomMargin = Util.dpToPx(context, 15);
        view.setLayoutParams(layoutParams);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);

        titleTextView.setText(reviewScoreQuestion.title);
        descriptionTextView.setText(reviewScoreQuestion.description);

        View emoticonView = view.findViewById(R.id.emoticonView);

        DailyEmoticonImageView emoticonImageView0 = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView0);
        DailyEmoticonImageView emoticonImageView1 = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView1);
        DailyEmoticonImageView emoticonImageView2 = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView2);
        DailyEmoticonImageView emoticonImageView3 = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView3);
        DailyEmoticonImageView emoticonImageView4 = (DailyEmoticonImageView) emoticonView.findViewById(R.id.emoticonImageView4);

        emoticonImageView0.setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        emoticonImageView1.setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        emoticonImageView2.setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        emoticonImageView3.setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        emoticonImageView4.setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");

        emoticonImageView0.setScaleX(0.55f);
        emoticonImageView0.setScaleY(0.55f);

        emoticonImageView1.setScaleX(0.55f);
        emoticonImageView1.setScaleY(0.55f);

        emoticonImageView2.setScaleX(0.55f);
        emoticonImageView2.setScaleY(0.55f);

        emoticonImageView3.setScaleX(0.55f);
        emoticonImageView3.setScaleY(0.55f);

        emoticonImageView4.setScaleX(0.55f);
        emoticonImageView4.setScaleY(0.55f);

        emoticonImageView0.setTranslationY(Util.dpToPx(mContext, 15));
        emoticonImageView1.setTranslationY(Util.dpToPx(mContext, 15));
        emoticonImageView2.setTranslationY(Util.dpToPx(mContext, 15));
        emoticonImageView3.setTranslationY(Util.dpToPx(mContext, 15));
        emoticonImageView4.setTranslationY(Util.dpToPx(mContext, 15));

        emoticonImageView0.setOnClickListener(this);
        emoticonImageView1.setOnClickListener(this);
        emoticonImageView2.setOnClickListener(this);
        emoticonImageView3.setOnClickListener(this);
        emoticonImageView4.setOnClickListener(this);

        emoticonImageView0.startAnimation();
        emoticonImageView1.startAnimation();
        emoticonImageView2.startAnimation();
        emoticonImageView3.startAnimation();
        emoticonImageView4.startAnimation();
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
    }

    public void startScoreClickedAnimation(final View view)
    {
        if (mSelectedEmoticonView != null && mSelectedEmoticonView.getId() == view.getId())
        {
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
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
            animatorSet.playTogether(scaleDownAnimator, scaleUpAnimator);
        } else if (scaleUpAnimator != null)
        {
            animatorSet.playTogether(scaleUpAnimator);
        }

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
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

        mSelectedView.setSelected(true);

        TextView resultTextView = (TextView) mSelectedView.findViewById(R.id.resultTextView);
        resultTextView.setSelected(true);

        switch (emoticonView.getId())
        {
            case R.id.emoticonImageView0:
                resultTextView.setText(R.string.label_review_score0);
                break;

            case R.id.emoticonImageView1:
                resultTextView.setText(R.string.label_review_score1);
                break;

            case R.id.emoticonImageView2:
                resultTextView.setText(R.string.label_review_score2);
                break;

            case R.id.emoticonImageView3:
                resultTextView.setText(R.string.label_review_score3);
                break;

            case R.id.emoticonImageView4:
                resultTextView.setText(R.string.label_review_score4);
                break;
        }

        DailyTextView titleTextView = (DailyTextView) mSelectedView.findViewById(R.id.titleTextView);
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
