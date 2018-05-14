/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewAnswerValue;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;

import java.util.ArrayList;

public class ReviewPickCardLayout extends ReviewCardLayout implements View.OnClickListener
{
    private View mSelectedView;
    private int mReviewPosition; // min : 1 ~ max : 항목개수
    private OnPickClickListener mOnPickClickListener;
    private GridLayout mGridLayout;

    public interface OnPickClickListener
    {
        /**
         * @param reviewCardLayout
         * @param selectedType     min : 1 ~ max : 항목개수
         */
        void onClick(ReviewCardLayout reviewCardLayout, int selectedType);
    }

    public ReviewPickCardLayout(Context context, int position, ReviewPickQuestion reviewPickQuestion)
    {
        super(context, position);

        initLayout(context, reviewPickQuestion);
    }

    private void initLayout(Context context, ReviewPickQuestion reviewPickQuestion)
    {
        // 카드 레이아웃
        setEnabled(false);
        setBackgroundResource(R.drawable.selector_review_cardlayout_enabled);

        final int DP1 = ScreenUtils.dpToPx(context, 1);
        setPadding(DP1, DP1, DP1, DP1);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = ScreenUtils.dpToPx(context, 15);
        setLayoutParams(layoutParams);

        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_pick, this);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

        titleTextView.setText(reviewPickQuestion.title);
        descriptionTextView.setText(reviewPickQuestion.description);

        mGridLayout = view.findViewById(R.id.gridLayout);

        if (mGridLayout.getChildCount() > 0)
        {
            mGridLayout.removeAllViews();
        }

        ArrayList<ReviewAnswerValue> reviewAnswerValueList = reviewPickQuestion.getAnswerValueList();

        int size = reviewAnswerValueList.size();

        for (int i = 0; i < size; i++)
        {
            ReviewAnswerValue reviewAnswerValue = reviewAnswerValueList.get(i);

            View pickItemView = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_pick_grid_item, null);
            pickItemView.setId(mGridLayout.getId() + i + 1);

            TextView gridItemTitleTextView = pickItemView.findViewById(R.id.titleTextView);
            TextView gridItemDescriptionTextView = pickItemView.findViewById(R.id.descriptionTextView);

            // 빈상자인 경우
            if (DailyTextUtils.isTextEmpty(reviewAnswerValue.code, reviewAnswerValue.description) == true)
            {
                gridItemTitleTextView.setVisibility(View.GONE);
                gridItemDescriptionTextView.setVisibility(View.GONE);
            } else
            {
                String[] titleSplits = reviewAnswerValue.description.split("\\n");

                gridItemTitleTextView.setText(titleSplits[0]);

                if (titleSplits.length == 2)
                {
                    gridItemDescriptionTextView.setVisibility(View.VISIBLE);
                    gridItemDescriptionTextView.setText(titleSplits[1]);
                } else
                {
                    gridItemDescriptionTextView.setVisibility(View.GONE);
                }
            }

            android.support.v7.widget.GridLayout.LayoutParams gridLayoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
            gridLayoutParams.width = 0;

            if (i >= size - 2)
            {
                gridLayoutParams.height = ScreenUtils.dpToPx(mContext, 56);
            } else
            {
                gridLayoutParams.height = ScreenUtils.dpToPx(mContext, 57);
            }

            gridLayoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            pickItemView.setLayoutParams(gridLayoutParams);

            if (DailyTextUtils.isTextEmpty(reviewAnswerValue.code, reviewAnswerValue.description) == false)
            {
                if (DailyTextUtils.isTextEmpty(reviewPickQuestion.selectedAnswerCode) == false //
                    && reviewPickQuestion.selectedAnswerCode.equalsIgnoreCase(reviewAnswerValue.code))
                {
                    setTempReviewValue(pickItemView);
                }

                pickItemView.setOnClickListener(this);
            }

            mGridLayout.addView(pickItemView);

            // 홀수 인 경우 세로 바를 짝수인 경우 하단 바를 넣는다.
            if (i % 2 == 0)
            {
                View dividerView = new View(mContext);
                android.support.v7.widget.GridLayout.LayoutParams dividerLayoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
                dividerLayoutParams.width = ScreenUtils.dpToPx(mContext, 1);
                dividerLayoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 0.0f);

                if (i >= size - 2)
                {
                    dividerLayoutParams.height = ScreenUtils.dpToPx(mContext, 56);
                } else
                {
                    dividerLayoutParams.height = ScreenUtils.dpToPx(mContext, 57);
                }

                dividerView.setBackgroundResource(R.color.default_line_cf0f0f0);
                dividerView.setLayoutParams(dividerLayoutParams);
                mGridLayout.addView(dividerView);
            }

            // 마지막인 경우 underLine을 보여주지 않는다.
            View underLineView = pickItemView.findViewById(R.id.underLineView);
            if (i >= size - 2)
            {
                underLineView.setVisibility(View.GONE);
            } else
            {
                underLineView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setOnPickClickListener(OnPickClickListener listener)
    {
        mOnPickClickListener = listener;
    }

    @Override
    public void onClick(View view)
    {
        if (mSelectedView != null && mSelectedView == view)
        {
            return;
        }

        if (mSelectedView != null)
        {
            DailyTextView gridItemTitleTextView = mSelectedView.findViewById(R.id.titleTextView);

            gridItemTitleTextView.setTypeface(FontManager.getInstance(mContext).getRegularTypeface());

            mSelectedView.setSelected(false);
        }

        setEnabled(true);
        view.setSelected(true);
        mSelectedView = view;

        DailyTextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_circular_check, 0);

        DailyTextView gridItemTitleTextView = view.findViewById(R.id.titleTextView);

        gridItemTitleTextView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());

        mReviewPosition = view.getId() - mGridLayout.getId();

        if (mOnPickClickListener != null)
        {
            mOnPickClickListener.onClick(this, mReviewPosition);
        }
    }

    @Override
    public boolean isChecked()
    {
        return mReviewPosition > 0;
    }

    @Override
    public Object getReviewValue()
    {
        return mReviewPosition;
    }

    private void setTempReviewValue(View view)
    {
        if (mSelectedView != null && view != null && mSelectedView == view)
        {
            return;
        }

        if (mSelectedView != null)
        {
            DailyTextView gridItemTitleTextView = mSelectedView.findViewById(R.id.titleTextView);

            gridItemTitleTextView.setTypeface(FontManager.getInstance(mContext).getRegularTypeface());

            mSelectedView.setSelected(false);
        }

        setEnabled(true);
        view.setSelected(true);
        mSelectedView = view;

        DailyTextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_circular_check, 0);

        DailyTextView gridItemTitleTextView = view.findViewById(R.id.titleTextView);

        gridItemTitleTextView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());

        mReviewPosition = view.getId() - mGridLayout.getId();
    }
}
