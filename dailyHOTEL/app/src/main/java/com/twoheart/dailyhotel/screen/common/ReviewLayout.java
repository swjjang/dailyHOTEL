/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;

public class ReviewLayout extends BaseLayout
{
    private DailyEmoticonImageView mDailyEmoticonImageView;

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
        int imageHeight = Util.getRatioHeightType4x3(Util.getLCDWidth(mContext));
        com.facebook.drawee.view.SimpleDraweeView simpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.placeImageView);
        ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        layoutParams.height = imageHeight;
        simpleDraweeView.setLayoutParams(layoutParams);


    }

    private View getReviewScoreView(Context context, ViewGroup viewGroup, ReviewScoreQuestion reviewScoreQuestion)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_row_review_score, viewGroup);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
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

        TextView resultTextView = (TextView) view.findViewById(R.id.resultTextView);

        return view;
    }
}
