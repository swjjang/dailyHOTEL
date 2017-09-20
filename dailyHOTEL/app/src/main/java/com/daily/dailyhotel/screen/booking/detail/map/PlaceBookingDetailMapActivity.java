package com.daily.dailyhotel.screen.booking.detail.map;


import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public abstract class PlaceBookingDetailMapActivity extends BaseActivity<PlaceBookingDetailMapPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_PLACEBOOKINGDAY = "placeBookingDay";
    protected static final String INTENT_EXTRA_DATA_PLACE_LIST = "placeList";
    protected static final String INTENT_EXTRA_DATA_PLACE_LOCATION = "placeLocation";
    protected static final String INTENT_EXTRA_DATA_PLACE_NAME = "placeName";
    protected static final String INTENT_EXTRA_DATA_CALL_BY_THANK_YOU = "callByThankYou";

    static final int REQUEST_CODE_PERMISSION_MANAGER = 10000;
    static final int REQUEST_CODE_SETTING_LOCATION = 10001;

    protected abstract PlaceBookingDetailMapPresenter getPlaceBookingDetailMapPresenter();

    //    public static Intent newInstance(Context context, String title, PlaceBookingDay placeBookingDay, ArrayList<? extends Place> list)
    //    {
    //        Intent intent = new Intent(context, PlaceBookingDetailMapActivity.class);
    //        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
    //        intent.putExtra(INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
    //        intent.putExtra(INTENT_EXTRA_DATA_PLACE_LIST, list);
    //        return intent;
    //    }

    @NonNull
    @Override
    protected PlaceBookingDetailMapPresenter createInstancePresenter()
    {
        return getPlaceBookingDetailMapPresenter();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
