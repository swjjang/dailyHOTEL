/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

public class ReviewCommentCardLayout extends ReviewCardLayout implements View.OnClickListener
{
    private TextView mCommentTextView;

    public ReviewCommentCardLayout(Context context, Constants.PlaceType placeType)
    {
        super(context);

        initLayout(context, placeType);
    }

    private void initLayout(Context context, Constants.PlaceType placeType)
    {
        setBackgroundResource(R.drawable.selector_review_cardlayout);

        final int DP1 = Util.dpToPx(context, 1);
        setPadding(DP1, DP1, DP1, DP1);

        int cardWidth = Util.getLCDWidth(mContext) - Util.dpToPx(context, 30);
        int cardHeight = Util.getRatioHeightType4x3(cardWidth);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = Util.dpToPx(context, 15);
        setLayoutParams(layoutParams);
        setMinimumHeight(cardHeight);

        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_comment, this);

        mCommentTextView = (TextView) view.findViewById(R.id.commentTextView);

        switch (placeType)
        {
            case HOTEL:
                mCommentTextView.setHint(R.string.message_review_comment_stay_hint);
                break;

            case FNB:
                mCommentTextView.setHint(R.string.message_review_comment_gourmet_hint);
                break;
        }
    }

    public void setReviewCommentView(String text)
    {
        if (mCommentTextView == null)
        {
            return;
        }

        mCommentTextView.setText(text);
    }

    @Override
    public void onClick(View view)
    {
    }
}
