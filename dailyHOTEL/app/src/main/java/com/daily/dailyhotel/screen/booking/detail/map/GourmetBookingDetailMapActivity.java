package com.daily.dailyhotel.screen.booking.detail.map;


import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class GourmetBookingDetailMapActivity extends PlaceBookingDetailMapActivity
{
    public static Intent newInstance(Context context, String title, GourmetBookingDay gourmetBookingDay //
        , ArrayList<Gourmet> gourmetList, Location location, String placeName, boolean isCallByThankYou)
    {
        Intent intent = new Intent(context, GourmetBookingDetailMapActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_LIST, gourmetList);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_NAME, placeName);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_BY_THANK_YOU, isCallByThankYou);

        return intent;
    }

    @Override
    protected PlaceBookingDetailMapPresenter getPlaceBookingDetailMapPresenter()
    {
        return new GourmetBookingDetailMapPresenter(this);
    }
}
