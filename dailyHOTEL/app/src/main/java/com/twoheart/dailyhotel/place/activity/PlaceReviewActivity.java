package com.twoheart.dailyhotel.place.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.PlaceReview;
import com.twoheart.dailyhotel.network.model.PlaceReviewScore;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceReviewLayout;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public abstract class PlaceReviewActivity extends BaseActivity
{
    private PlaceReviewLayout mPlaceReviewLayout;

    protected abstract
    @NonNull
    PlaceReviewLayout createInstanceLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mPlaceReviewLayout = createInstanceLayout();

        setContentView(mPlaceReviewLayout.onCreateView(R.layout.activity_place_review));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void setReviewScores(List<PlaceReviewScore> placeReviewScoreList)
    {
        if (mPlaceReviewLayout == null)
        {
            return;
        }

        mPlaceReviewLayout.setReviewScores(placeReviewScoreList);
    }

    public void addReviewList(List<PlaceReview> placeReviewList)
    {
        if (mPlaceReviewLayout == null)
        {
            return;
        }

        mPlaceReviewLayout.addReviewList(placeReviewList);
    }
}
