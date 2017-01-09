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

public abstract class ReviewCardLayout extends RelativeLayout
{
    protected Context mContext;
    protected int position;

    public ReviewCardLayout(Context context, int position)
    {
        super(context);

        mContext = context;
        this.position = position;
    }

    public abstract boolean isChecked();

    public abstract Object getReviewValue();
}
