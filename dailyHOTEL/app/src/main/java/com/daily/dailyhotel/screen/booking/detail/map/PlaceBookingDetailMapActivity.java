package com.daily.dailyhotel.screen.booking.detail.map;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class PlaceBookingDetailMapActivity extends BaseActivity<PlaceBookingDetailMapPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";
    protected static final String INTENT_EXTRA_DATA_PLACE_LIST = "placeList";

    public static Intent newInstance(Context context, String title, PlaceBookingDay placeBookingDay, ArrayList<? extends Place> list)
    {
        Intent intent = new Intent(context, PlaceBookingDetailMapActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_LIST, list);
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
    protected PlaceBookingDetailMapPresenter createInstancePresenter()
    {
        return new PlaceBookingDetailMapPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
