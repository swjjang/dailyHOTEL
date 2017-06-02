package com.daily.dailyhotel.screen.stay.outbound.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundThankYouActivity extends BaseActivity<StayOutboundThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_ROOM_PRICE = "roomPrice";
    static final String INTENT_EXTRA_DATA_CHECKIN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECKOUT = "checkOut";
    static final String INTENT_EXTRA_DATA_CHECKIN_TIME = "checkInTime";
    static final String INTENT_EXTRA_DATA_CHECKOUT_TIME = "checkOutTime";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE = "roomType";

    public static Intent newInstance(Context context, int stayIndex, String stayName, String imageUrl, int roomPrice//
        , String checkInDateTime, String checkOutDateTime, String checkInTime, String checkOutTime, int numberOfAdults//
        , ArrayList<Integer> childAgeList, String roomType)
    {
        Intent intent = new Intent(context, StayOutboundThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_PRICE, roomPrice);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN_TIME, checkInTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT_TIME, checkOutTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childAgeList);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE, roomType);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayOutboundThankYouPresenter createInstancePresenter()
    {
        return new StayOutboundThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
