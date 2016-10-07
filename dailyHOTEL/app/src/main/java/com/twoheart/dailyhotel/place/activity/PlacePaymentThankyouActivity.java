package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.networkcontroller.PlacePaymentThankyouNetworkController;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.Map;

public abstract class PlacePaymentThankyouActivity extends BaseActivity implements OnClickListener
{
    protected static final String INTENT_EXTRA_DATA_IMAGEURL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_PLACE_NAME = "placeName";
    protected static final String INTENT_EXTRA_DATA_PLACE_TYPE = "placeType";
    protected static final String INTENT_EXTRA_DATA_USER_NAME = "userName";
    protected static final String INTENT_EXTRA_DATA_CHECK_IN_DATE = "checkIn";
    protected static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE = "checkOut";
    protected static final String INTENT_EXTRA_DATA_NIGHTS = "nights";
    protected static final String INTENT_EXTRA_DATA_VISIT_TIME = "visitTime";
    protected static final String INTENT_EXTRA_DATA_PRODUCT_COUNT = "productCount";
    protected static final String INTENT_EXTRA_DATA_PAYMENT_TYPE = "paymentType";
    protected static final String INTENT_EXTRA_DATA_DISCOUNT_TYPE = "discountType";
    protected static final String INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM = "mapPaymentInform";

    private String mPaymentType;
    private Map<String, String> mParams;

    protected abstract void recordEvent(String action, String label);

    protected abstract void onFirstPurchaseSuccess(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params);

    protected abstract void onCouponUsedPurchase(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params);

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
        String placeName = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_NAME);
        String placeType = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_TYPE);
        String userName = intent.getStringExtra(INTENT_EXTRA_DATA_USER_NAME);

        String discountType = intent.getStringExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE);

        mParams = (Map<String, String>) intent.getSerializableExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM);

        String productIndex = mParams.get(AnalyticsManager.KeyType.TICKET_INDEX);

        initToolbar();
        initLayout(imageUrl, placeName, placeType, userName);

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

    private void initLayout(String imageUrl, String place, String placeType, String userName)
    {
        if (Util.isTextEmpty(place, placeType, userName) == true)
        {
            Util.restartApp(this);
            return;
        }

        int imageHeight = Util.getRatioHeightType4x3(Util.getLCDWidth(this));
        ExLog.d("height : " + imageHeight);
        com.facebook.drawee.view.SimpleDraweeView simpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.placeImageView);
        ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        layoutParams.height = imageHeight;
        simpleDraweeView.setLayoutParams(layoutParams);

        TextView placeTextView = (TextView) findViewById(R.id.bookingPlaceTextView);
        TextView placeTypeTextView = (TextView) findViewById(R.id.productTypeTextView);
        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        View confirmView = findViewById(R.id.confirmView);

        Util.requestImageResize(this, simpleDraweeView, imageUrl);
        placeTextView.setText(place);
        placeTypeTextView.setText(placeType);

        String message;
        if (Util.isTextEmpty(userName) == false)
        {
            message = getString(R.string.message_completed_payment_format, userName);
            SpannableStringBuilder userNameBuilder = new SpannableStringBuilder(message);
            userNameBuilder.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(this).getMediumTypeface()),//
                0, userName.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageTextView.setText(userNameBuilder);
        } else
        {
            message = getString(R.string.message_completed_payment_default);
            messageTextView.setText(message);
        }

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
        public void onUserTracking(int hotelPaymentCompletedCount, int gourmetPaymentCompletedCount)
        {
            boolean isFirstStayPurchase = hotelPaymentCompletedCount == 1 ? true : false;
            boolean isFirstGourmetPurchase = gourmetPaymentCompletedCount == 1 ? true : false;

            if (isFirstStayPurchase == true || isFirstGourmetPurchase == true)
            {
                PlacePaymentThankyouActivity.this.onFirstPurchaseSuccess(isFirstStayPurchase, isFirstGourmetPurchase, mPaymentType, mParams);
            }

            boolean isCouponUsed = false;

            if (mParams != null && mParams.containsKey(AnalyticsManager.KeyType.COUPON_REDEEM) == true)
            {
                try
                {
                    isCouponUsed = Boolean.parseBoolean(mParams.get(AnalyticsManager.KeyType.COUPON_REDEEM));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            if (isCouponUsed == true)
            {
                PlacePaymentThankyouActivity.this.onCouponUsedPurchase(isFirstStayPurchase, isFirstGourmetPurchase, mPaymentType, mParams);
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
