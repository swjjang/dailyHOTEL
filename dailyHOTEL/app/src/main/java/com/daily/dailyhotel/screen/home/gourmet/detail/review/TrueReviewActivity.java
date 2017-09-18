package com.daily.dailyhotel.screen.home.gourmet.detail.review;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.parcel.ReviewScoresParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetTrueReviewAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class TrueReviewActivity extends BaseActivity<TrueReviewPresenter>
{
    static final String INTENT_EXTRA_DATA_GOURMET_INDEX = "gourmetIndex";
    static final String INTENT_EXTRA_DATA_REVIEW_SCORES = "reviewScores";

    public static Intent newInstance(Context context, int gourmetIndex, ReviewScores reviewScores, GourmetTrueReviewAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, TrueReviewActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_INDEX, gourmetIndex);
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
    protected TrueReviewPresenter createInstancePresenter()
    {
        return new TrueReviewPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
