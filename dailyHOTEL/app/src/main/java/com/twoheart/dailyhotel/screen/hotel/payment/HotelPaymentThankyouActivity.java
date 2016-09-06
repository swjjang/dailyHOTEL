package com.twoheart.dailyhotel.screen.hotel.payment;

import android.content.Context;
import android.content.Intent;
import android.view.View.OnClickListener;

import com.twoheart.dailyhotel.place.activity.PlacePaymentThankyouActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.io.Serializable;
import java.util.Map;

public class HotelPaymentThankyouActivity extends PlacePaymentThankyouActivity implements OnClickListener
{
    public static Intent newInstance(Context context, String imageUrl, String placeType, String date, String paymentType, String discountType, Map<String, String> params)
    {
        Intent intent = new Intent(context, HotelPaymentThankyouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_TYPE, placeType);
        intent.putExtra(INTENT_EXTRA_DATA_DATE, date);
        intent.putExtra(INTENT_EXTRA_DATA_PAYMENT_TYPE, paymentType);
        intent.putExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE, discountType);
        intent.putExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM, (Serializable) params);

        return intent;
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_THANKYOU);

        super.onStart();
    }

    @Override
    protected void recordEvent(String action, String label)
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, action, label, null);
    }

    @Override
    protected void onFirstPurchaseSuccess(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params)
    {
        if (isFirstStayPurchase == true)
        {
            recordEvent(AnalyticsManager.Action.FIRST_PURCHASE_SUCCESS, paymentType);

            AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS, params);
        }
    }
}
