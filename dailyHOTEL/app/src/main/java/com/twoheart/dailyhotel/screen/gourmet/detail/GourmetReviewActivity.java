package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.activity.PlaceReviewActivity;
import com.twoheart.dailyhotel.util.Constants;

public class GourmetReviewActivity extends PlaceReviewActivity
{
    public static Intent newInstance(Context context, int placeIndex, PlaceReviewScores placeReviewScore)
    {
        Intent intent = new Intent(context, GourmetReviewActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();
    }

    @Override
    protected PlaceType getPlaceType()
    {
        return PlaceType.FNB;
    }
}
