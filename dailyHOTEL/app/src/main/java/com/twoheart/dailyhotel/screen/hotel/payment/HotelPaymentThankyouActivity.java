package com.twoheart.dailyhotel.screen.hotel.payment;

import android.content.Context;
import android.content.Intent;
import android.view.View.OnClickListener;

import com.twoheart.dailyhotel.place.activity.PlacePaymentThankyouActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class HotelPaymentThankyouActivity extends PlacePaymentThankyouActivity implements OnClickListener
{
    public static Intent newInstance(Context context, String imageUrl, String place, String placeType, String date)
    {
        Intent intent = new Intent(context, HotelPaymentThankyouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE, place);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_TYPE, placeType);
        intent.putExtra(INTENT_EXTRA_DATA_DATEL, date);

        return intent;
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_THANKYOU, null);

        super.onStart();
    }

    @Override
    protected void recordEvent(String action, String label)
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, action, label, null);
    }
}
