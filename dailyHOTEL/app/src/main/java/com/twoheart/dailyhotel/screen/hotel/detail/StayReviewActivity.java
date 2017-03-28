package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.activity.PlaceReviewActivity;
import com.twoheart.dailyhotel.util.Constants;

public class StayReviewActivity extends PlaceReviewActivity
{
    public static Intent newInstance(Context context, int placeIndex, PlaceReviewScores placeReviewScore)
    {
        Intent intent = new Intent(context, StayReviewActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACE_REVIEW_SCORES, placeReviewScore);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        int placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, 0);
        PlaceReviewScores placeReviewScores = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACE_REVIEW_SCORES);

        if (placeIndex == 0)
        {
            finish();
            return;
        }

        setPlaceIndex(placeIndex);
        setReviewScores(placeReviewScores.reviewScoreAvgs);
    }

    @Override
    protected PlaceType getPlaceType()
    {
        return PlaceType.HOTEL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();
    }
}
