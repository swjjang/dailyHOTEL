package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.networkcontroller.PlacePaymentThankyouNetworkController;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Map;

public abstract class PlacePaymentThankyouActivity extends BaseActivity implements OnClickListener
{
    protected static final String INTENT_EXTRA_DATA_IMAGEURL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_PLACE_TYPE = "placeType";
    protected static final String INTENT_EXTRA_DATA_DATEL = "date";
    protected static final String INTENT_EXTRA_DATA_PAYMENT_TYPE = "paymentType";
    protected static final String INTENT_EXTRA_DATA_DISCOUNT_TYPE = "discountType";
    protected static final String INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM = "mapPaymentInform";

    private String mPaymentType;

    protected abstract void recordEvent(String action, String label);

    protected abstract void onFirstPurchaseSuccess(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_result);

        Intent intent = getIntent();

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        mPaymentType = intent.getStringExtra(INTENT_EXTRA_DATA_PAYMENT_TYPE);
        String imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGEURL);
        String placeType = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_TYPE);
        String date = intent.getStringExtra(INTENT_EXTRA_DATA_DATEL);
        String discountType = intent.getStringExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE);



//        params.put(AnalyticsManager.KeyType.NAME, ticketInformation.placeName);
//        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetPaymentInformation.placeIndex));
//        params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(ticketInformation.discountPrice));
//        params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(gourmetPaymentInformation.ticketCount));
//        params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
//        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetPaymentInformation.placeIndex));
//        params.put(AnalyticsManager.KeyType.TICKET_NAME, ticketInformation.name);
//        params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(ticketInformation.index));
//        params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
//        params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(ticketInformation.discountPrice * gourmetPaymentInformation.ticketCount));
//        params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
//        params.put(AnalyticsManager.KeyType.CATEGORY, gourmetPaymentInformation.category);
//        params.put(AnalyticsManager.KeyType.DBENEFIT, gourmetPaymentInformation.isDBenefit ? "yes" : "no");
//        params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, gourmetPaymentInformation.paymentType.getName());

        Map<String, String> params = (Map<String, String>) intent.getSerializableExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM);

        String productIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX);
        String place = params.get(AnalyticsManager.KeyType.NAME);
        ExLog.d("thank map : " + params.toString());


        initToolbar();
        initLayout(imageUrl, place, placeType, date);

        recordEvent(AnalyticsManager.Action.END_PAYMENT, mPaymentType);
        recordEvent(AnalyticsManager.Action.PAYMENT_USED, discountType);
        recordEvent(AnalyticsManager.Action.PRODUCT_ID, productIndex);

        PlacePaymentThankyouNetworkController networkController = new PlacePaymentThankyouNetworkController(this, mNetworkTag, mNetworkControllerListener);
        networkController.requestUserTracking();
    }

    private void initToolbar()
    {
        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);
    }

    private void initLayout(String imageUrl, String place, String placeType, String date)
    {
        if (Util.isTextEmpty(place, placeType, date) == true)
        {
            Util.restartApp(this);
            return;
        }

        com.facebook.drawee.view.SimpleDraweeView simpleDraweeVie = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.placeImageView);
        TextView placeTextView = (TextView) findViewById(R.id.bookingPlaceTextView);
        TextView placeTypeTextView = (TextView) findViewById(R.id.placeTypeTextView);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        View confirmView = findViewById(R.id.confirmView);

        Util.requestImageResize(this, simpleDraweeVie, imageUrl);
        placeTextView.setText(place);
        placeTypeTextView.setText(placeType);
        dateTextView.setText(date);

        confirmView.setOnClickListener(this);
    }

    @Override
    public void finish()
    {
        setResult(RESULT_OK);

        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED);
                finish();
                break;
            case R.id.confirmView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.VIEW_BOOKING_STATUS_CLICKED);
                finish();
                break;
        }
    }

    private PlacePaymentThankyouNetworkController.OnNetworkControllerListener mNetworkControllerListener = new PlacePaymentThankyouNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserTracking(int hotelPaymentCompletedCount, int hotelUsedCount, int gourmetPaymentCompletedCount, int gourmetUsedCount)
        {
            boolean isFirstStayPurchase = hotelPaymentCompletedCount == 1 ? true : false;
            boolean isFirstGourmetPurchase = gourmetPaymentCompletedCount == 1 ? true : false;

            if (isFirstStayPurchase == true || isFirstGourmetPurchase == true)
            {
                PlacePaymentThankyouActivity.this.onFirstPurchaseSuccess(isFirstStayPurchase, isFirstGourmetPurchase, mPaymentType);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            // do nothing
        }

        @Override
        public void onError(Exception e)
        {
            // do nothing
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            // do nothing
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            // do nothing
        }
    };
}
