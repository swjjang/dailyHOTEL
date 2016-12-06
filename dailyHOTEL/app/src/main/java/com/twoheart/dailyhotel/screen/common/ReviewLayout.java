/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.common;

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
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;

public class ReviewLayout extends BaseLayout implements View.OnClickListener, NestedScrollView.OnScrollChangeListener
{
    private View mToolbar, mImageDimView;
    private NestedScrollView mNestedScrollView;
    private ViewGroup mScrollLayout;
    private SimpleDraweeView mPlaceImaegView;
    private DailyEmoticonImageView[] mDailyEmoticonImageView;
    private View mSelectedEmoticonView, mSelectedView;
    private TextView mPlaceNameTextView, mPeriodTextView;
    private TextView mToolbarTitle, mToolbarSubTitle;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onReviewScoreTypeClick();

        void onReviewPickTypeClick();

        void onReviewTextClick();
    }

    public ReviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        mScrollLayout = (ViewGroup) mNestedScrollView.findViewById(R.id.scrollLayout);

        mImageDimView = view.findViewById(R.id.imageDimView);
        mImageDimView.setAlpha(0.0f);

        int imageHeight = Util.getRatioHeightType4x3(Util.getLCDWidth(mContext));
        mPlaceImaegView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.placeImageView);
        ViewGroup.LayoutParams layoutParams = mPlaceImaegView.getLayoutParams();
        layoutParams.height = imageHeight;
        mPlaceImaegView.setLayoutParams(layoutParams);

        mPlaceNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
        mPeriodTextView = (TextView) view.findViewById(R.id.periodTextView);

        mNestedScrollView.setOnScrollChangeListener(this);
    }

    private void initToolbar(View view)
    {
        mToolbar = view.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        mToolbarSubTitle = (TextView) mToolbar.findViewById(R.id.toolbarSubTitle);

        mToolbarTitle.setAlpha(0.0f);
        mToolbarSubTitle.setAlpha(0.0f);

        View closeView = mToolbar.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    public void setPlaceImageUrl(Context context, String imageUrl)
    {
        if (mPlaceImaegView == null)
        {
            return;
        }

        Util.requestImageResize(context, mPlaceImaegView, imageUrl);
    }

    public void setPlaceInformation(String placeName, String period)
    {
        if (mPlaceNameTextView == null || mPeriodTextView == null)
        {
            return;
        }

        mPlaceNameTextView.setText(placeName);
        mPeriodTextView.setText(period);

        mToolbarTitle.setText(placeName);
        mToolbarSubTitle.setText(period);
    }

    public void addScrollLayout(View view)
    {
        if (mScrollLayout == null)
        {
            return;
        }

        mScrollLayout.addView(view);
    }

    public void setSelectedView(int position)
    {
        if (mScrollLayout == null || mScrollLayout.getChildCount() <= position)
        {
            return;
        }

        stopAnimation();

        mSelectedView = mScrollLayout.getChildAt(position);

        mNestedScrollView.smoothScrollTo(0, (int)mSelectedView.getY());

        mDailyEmoticonImageView = new DailyEmoticonImageView[5];

        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) mSelectedView.findViewById(R.id.emoticonImageView0);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) mSelectedView.findViewById(R.id.emoticonImageView1);
        mDailyEmoticonImageView[2] = (DailyEmoticonImageView) mSelectedView.findViewById(R.id.emoticonImageView2);
        mDailyEmoticonImageView[3] = (DailyEmoticonImageView) mSelectedView.findViewById(R.id.emoticonImageView3);
        mDailyEmoticonImageView[4] = (DailyEmoticonImageView) mSelectedView.findViewById(R.id.emoticonImageView4);

        for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
        {
            dailyEmoticonImageView.setOnClickListener(this);
        }
    }

    public void startAnimation()
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

    public void stopAnimation()
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

    public View getReviewScoreView(Context context, ReviewScoreQuestion reviewScoreQuestion)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_score, null);

        int cardWidth = Util.getLCDWidth(mContext) - Util.dpToPx(context, 30);
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

        return view;
    }

    @Override
    public void onClick(final View v)
    {
        if (mSelectedEmoticonView != null && mSelectedEmoticonView.getId() == v.getId())
        {
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator scaleDownAnimator = null, scaleUpAnimator = null;

        if (mSelectedEmoticonView != null)
        {
            scaleDownAnimator = getScaleDownAnimator(mSelectedEmoticonView);
        }

        checkedReviewEmoticon(v);

        mSelectedEmoticonView = v;

        if (v != null)
        {
            scaleUpAnimator = getScaleUpAnimator(v);
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

        ((OnEventListener)mOnEventListener).onReviewScoreTypeClick();
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
    {
        int toolbarHeight = mToolbar.getHeight();

        if (toolbarHeight >= scrollY)
        {
            float vectorValue = (float) scrollY / toolbarHeight;

            mPlaceImaegView.setTranslationY(-scrollY * 0.5f);

            int alphaValue = (int) (0x4d * vectorValue);
            mToolbar.setBackgroundColor((alphaValue << 24) & 0xff000000);

            float textAlphaValue = 1 - vectorValue * 2;
            textAlphaValue = textAlphaValue < 0 ? 0 : textAlphaValue;
            mPlaceNameTextView.setAlpha(textAlphaValue);
            mPeriodTextView.setAlpha(textAlphaValue);

            mToolbarTitle.setAlpha(vectorValue);
            mToolbarSubTitle.setAlpha(vectorValue);
        } else
        {
            mPlaceImaegView.setTranslationY(-toolbarHeight * 0.5f);
            mToolbar.setBackgroundColor(0x4f000000);

            mPlaceNameTextView.setAlpha(0.0f);
            mPeriodTextView.setAlpha(0.0f);

            mToolbarTitle.setAlpha(1.0f);
            mToolbarSubTitle.setAlpha(1.0f);
        }

        // 배경 없어지는 애니메이션
        int scrollTopY = mScrollLayout.getPaddingTop() - Util.dpToPx(mContext, 15);

        if(scrollTopY >= scrollY)
        {
            float vectorValue = (float) scrollY / scrollTopY;

            mImageDimView.setAlpha(vectorValue);
        } else
        {
            mImageDimView.setAlpha(1.0f);
        }
    }

    private int getEmptyReviewCard(int position)
    {
        if(mScrollLayout.getChildCount() <= position)
        {
            return -1;
        }
    }

    private void checkedReviewEmoticon(View emoticonView)
    {
        mSelectedView.setSelected(true);

        TextView resultTextView = (TextView) mSelectedView.findViewById(R.id.resultTextView);
        resultTextView.setSelected(true);

        switch (emoticonView.getId())
        {
            case R.id.emoticonImageView0:
                resultTextView.setText(R.string.review_score0);
                break;

            case R.id.emoticonImageView1:
                resultTextView.setText(R.string.review_score1);
                break;

            case R.id.emoticonImageView2:
                resultTextView.setText(R.string.review_score2);
                break;

            case R.id.emoticonImageView3:
                resultTextView.setText(R.string.review_score3);
                break;

            case R.id.emoticonImageView4:
                resultTextView.setText(R.string.review_score4);
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
