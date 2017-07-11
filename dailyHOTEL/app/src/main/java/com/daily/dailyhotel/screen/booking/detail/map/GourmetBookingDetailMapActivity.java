package com.daily.dailyhotel.screen.booking.detail.map;


import android.content.Context;
import android.content.Intent;

import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class GourmetBookingDetailMapActivity extends PlaceBookingDetailMapActivity
{
    public static Intent newInstance(Context context, String title, GourmetBookingDay gourmetBookingDay, ArrayList<Gourmet> gourmetList)
    {
        Intent intent = new Intent(context, GourmetBookingDetailMapActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_LIST, gourmetList);
        return intent;
    }

    @Override
    protected PlaceBookingDetailMapPresenter getPlaceBookingDetailMapPresenter()
    {
        return new GourmetBookingDetailMapPresenter(this);
    }
}
