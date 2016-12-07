/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class ReviewCardLayout extends RelativeLayout
{
    protected Context mContext;
    protected TextView mTitleTextView;
    protected TextView mDescriptionTextView;

    public ReviewCardLayout(Context context)
    {
        super(context);

        mContext = context;
    }
}
