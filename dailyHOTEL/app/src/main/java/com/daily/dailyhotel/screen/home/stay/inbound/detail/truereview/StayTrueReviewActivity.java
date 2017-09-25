package com.daily.dailyhotel.screen.home.stay.inbound.detail.truereview;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.parcel.ReviewScoresParcel;
import com.daily.dailyhotel.parcel.analytics.TrueReviewAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTrueReviewActivity extends BaseActivity<StayTrueReviewPresenter>
{
    static final int REQUEST_CODE_REVIEW_TERMS = 10000;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_REVIEW_SCORES = "reviewScores";

    public static Intent newInstance(Context context, int stayIndex, ReviewScores reviewScores, TrueReviewAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayTrueReviewActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_REVIEW_SCORES, new ReviewScoresParcel(reviewScores));
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayTrueReviewPresenter createInstancePresenter()
    {
        return new StayTrueReviewPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
