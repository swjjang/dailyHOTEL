package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlacePaymentThankyouActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.io.Serializable;
import java.util.Map;

public class GourmetPaymentThankyouActivity extends PlacePaymentThankyouActivity implements OnClickListener
{
    public static Intent newInstance(Context context, String imageUrl, String placeName, String placeType, //
                                     String userName, String date, String visitTime, int productCount, //
                                     String paymentType, Map<String, String> map)
    {
        Intent intent = new Intent(context, GourmetPaymentThankyouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_NAME, placeName);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_TYPE, placeType);
        intent.putExtra(INTENT_EXTRA_DATA_USER_NAME, userName);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, date);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_TIME, visitTime);
        intent.putExtra(INTENT_EXTRA_DATA_PRODUCT_COUNT, productCount);
        intent.putExtra(INTENT_EXTRA_DATA_PAYMENT_TYPE, paymentType);
        intent.putExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE, AnalyticsManager.Label.FULL_PAYMENT);

        intent.putExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM, (Serializable) map);

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

        String date = intent.getStringExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE);
        String visitTime = intent.getStringExtra(INTENT_EXTRA_DATA_VISIT_TIME);
        int productCount = intent.getIntExtra(INTENT_EXTRA_DATA_PRODUCT_COUNT, 0);

        View dateLayout = findViewById(R.id.dateInformationLayout);
        initDateLayout(dateLayout, date, visitTime, productCount);

        View textLayout = findViewById(R.id.textInformationLayout);
        initTextLayout(textLayout, productCount);
    }

    private void initDateLayout(View view, String date, String visitTime, int productCount)
    {
        if (view == null)
        {
            return;
        }

        if (Util.isTextEmpty(date, visitTime) == true)
        {
            return;
        }

        TextView dateTitleView = (TextView) view.findViewById(R.id.checkInDateTitleView);
        TextView dateTextView = (TextView) view.findViewById(R.id.checkInDateTextView);
        TextView visitTimeTitleView = (TextView) view.findViewById(R.id.checkOutDateTitleView);
        TextView visitTimeTextView = (TextView) view.findViewById(R.id.checkOutDateTextView);
        TextView nightsTextView = (TextView) view.findViewById(R.id.nightsTextView);

        dateTitleView.setText(R.string.label_visit_day);
        visitTimeTitleView.setText(R.string.label_booking_select_ticket_time);
        nightsTextView.setVisibility(View.GONE);

        dateTextView.setText(date);
        visitTimeTextView.setText(visitTime);
    }

    private void initTextLayout(View view, int productCount)
    {
        if (view == null)
        {
            return;
        }

        TextView bookingInfoTextView = (TextView) view.findViewById(R.id.bookingInfomationTextView);
        TextView bookingPlaceView = (TextView) view.findViewById(R.id.bookingPlaceView);
        TextView productTypeView = (TextView) view.findViewById(R.id.productTypeView);

        View productCountLayout = view.findViewById(R.id.productCountLayout);
        productCountLayout.setVisibility(View.VISIBLE);

        bookingInfoTextView.setText(R.string.label_booking_ticket_info);
        bookingPlaceView.setText(R.string.label_booking_place_name);
        productTypeView.setText(R.string.label_booking_ticket_type);

        TextView productCountTextView = (TextView) view.findViewById(R.id.productCountTextView);
        productCountTextView.setText(getString(R.string.label_booking_count, productCount));
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_THANKYOU);

        super.onStart();
    }

    @Override
    protected void recordEvent(String action, String label)
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, action, label, null);
    }

    @Override
    protected void onFirstPurchaseSuccess(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params)
    {
        if (isFirstGourmetPurchase == true)
        {
            recordEvent(AnalyticsManager.Action.FIRST_PURCHASE_SUCCESS, paymentType);

            AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS, params);
        }
    }
}
