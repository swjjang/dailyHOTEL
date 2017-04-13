package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.activity.PlaceReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class GourmetReviewActivity extends PlaceReviewActivity
{
    private String mCategory;

    public static Intent newInstance(Context context, int placeIndex, String category, PlaceReviewScores placeReviewScore)
    {
        Intent intent = new Intent(context, GourmetReviewActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CATEGORY, category);
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
        mCategory = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);
        PlaceReviewScores placeReviewScores = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACE_REVIEW_SCORES);

        if (placeIndex == 0)
        {
            finish();
            return;
        }

        setPlaceIndex(placeIndex);
        setReviewScores(getPlaceType(), placeReviewScores.reviewScoreAvgs);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.CATEGORY, mCategory);

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.TRUE_REVIEW_LIST, null, params);
    }

    @Override
    public void finish()
    {
        super.finish();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.TRUE_REVIEW_BACK_BUTTON_CLICK, AnalyticsManager.Label.GOURMET, null);
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
