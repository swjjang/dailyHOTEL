/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

public class ReviewLayout extends BaseLayout implements View.OnClickListener, NestedScrollView.OnScrollChangeListener
{
    private View mToolbar, mImageDimView;
    private NestedScrollView mNestedScrollView;
    private ViewGroup mScrollLayout;
    private SimpleDraweeView mPlaceImaegView;
    private TextView mPlaceNameTextView, mPeriodTextView;
    private TextView mToolbarTitle, mToolbarSubTitle;
    private TextView mConfirmTextView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onReviewScoreTypeClick(View view);

        void onReviewPickTypeClick(View view);

        void onReviewCommentClick(View view);

        void onConfirmClick();
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

        mConfirmTextView = (TextView) view.findViewById(R.id.confirmTextView);
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

    public View getReviewScoreView(Context context, ReviewScoreQuestion reviewScoreQuestion)
    {
        return new ReviewScoreCardLayout(mContext, reviewScoreQuestion);
    }

    public View getReviewPickView(Context context, ReviewPickQuestion reviewPickQuestion)
    {
        return new ReviewPickCardLayout(mContext, reviewPickQuestion);
    }

    public View getReviewCommentView(Context context, Constants.PlaceType placeType)
    {
        return new ReviewCommentCardLayout(mContext, placeType);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.confirmTextView:
                ((OnEventListener) mOnEventListener).onConfirmClick();
                break;

        }
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

        if (scrollTopY >= scrollY)
        {
            float vectorValue = (float) scrollY / scrollTopY;

            mImageDimView.setAlpha(vectorValue);
        } else
        {
            mImageDimView.setAlpha(1.0f);
        }
    }
}
