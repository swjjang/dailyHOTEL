package com.twoheart.dailyhotel.screen.hotel.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.activity.PlacePaymentThankyouActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.FontManager;

import java.io.Serializable;
import java.util.Map;

public class HotelPaymentThankyouActivity extends PlacePaymentThankyouActivity implements OnClickListener
{
    public static Intent newInstance(Context context, String imageUrl, String placeName, String placeType, //
                                     String userName, StayBookingDay stayBookingDay, //
                                     String paymentType, String discountType, Map<String, String> params)
    {
        Intent intent = new Intent(context, HotelPaymentThankyouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_NAME, placeName);
        intent.putExtra(INTENT_EXTRA_DATA_PLACE_TYPE, placeType);
        intent.putExtra(INTENT_EXTRA_DATA_USER_NAME, userName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_PAYMENT_TYPE, paymentType);
        intent.putExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE, discountType);
        intent.putExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM, (Serializable) params);

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

        StayBookingDay stayBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

        View dateLayout = findViewById(R.id.dateInformationLayout);
        initDateLayout(dateLayout, stayBookingDay);

        View textLayout = findViewById(R.id.textInformationLayout);
        initTextLayout(textLayout);

        if (isStampEnabled() == true)
        {
            setStampLayout(DailyPreference.getInstance(this).getRemoteConfigStampStayThankYouMessage1()//
                , DailyPreference.getInstance(this).getRemoteConfigStampStayThankYouMessage2()//
                , DailyPreference.getInstance(this).getRemoteConfigStampStayThankYouMessage3());
        }

    }

    private void initDateLayout(View view, StayBookingDay stayBookingDay)
    {
        if (view == null || stayBookingDay == null)
        {
            return;
        }

        TextView checkInDateTitleView = (TextView) view.findViewById(R.id.checkInDateTitleView);
        TextView checkInDateTextView = (TextView) view.findViewById(R.id.checkInDateTextView);
        TextView checkOutDateTitleView = (TextView) view.findViewById(R.id.checkOutDateTitleView);
        TextView checkOutDateTextView = (TextView) view.findViewById(R.id.checkOutDateTextView);
        TextView nightsTextView = (TextView) view.findViewById(R.id.nightsTextView);

        checkInDateTitleView.setText(R.string.act_booking_chkin);
        checkOutDateTitleView.setText(R.string.act_booking_chkout);

        String checkInDate = stayBookingDay.getCheckInDay("yyyy.M.d (EEE) HH시");
        String checkOutDate = stayBookingDay.getCheckOutDay("yyyy.M.d (EEE) HH시");

        SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDate);
        checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(this).getMediumTypeface()),//
            checkInDate.length() - 3, checkInDate.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkInDateTextView.setText(checkInSpannableStringBuilder);

        SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDate);
        checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(this).getMediumTypeface()),//
            checkOutDate.length() - 3, checkOutDate.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkOutDateTextView.setText(checkOutSpannableStringBuilder);

        try
        {
            nightsTextView.setText(this.getString(R.string.label_nights, stayBookingDay.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void initTextLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView bookingInfoTextView = (TextView) view.findViewById(R.id.bookingInfomationTextView);
        TextView bookingPlaceView = (TextView) view.findViewById(R.id.bookingPlaceView);
        TextView productTypeView = (TextView) view.findViewById(R.id.productTypeView);

        View productCountLayout = view.findViewById(R.id.productCountLayout);
        productCountLayout.setVisibility(View.GONE);

        bookingInfoTextView.setText(R.string.label_booking_room_info);
        bookingPlaceView.setText(R.string.label_booking_place_name);
        productTypeView.setText(R.string.label_booking_room_type);

    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_THANKYOU, null);

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

            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS, null, params);
        }
    }

    @Override
    protected void onCouponUsedPurchase(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params)
    {
        params.put(AnalyticsManager.KeyType.FIRST_PURCHASE, isFirstStayPurchase ? "y" : "n");
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, paymentType);
        AnalyticsManager.getInstance(this).purchaseWithCoupon(params);
    }

    @Override
    protected boolean isStampEnabled()
    {
        return DailyPreference.getInstance(this).isRemoteConfigStampEnabled();
    }
}
