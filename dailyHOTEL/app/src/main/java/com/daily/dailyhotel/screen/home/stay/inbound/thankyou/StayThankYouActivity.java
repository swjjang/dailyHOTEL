package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayThankYouActivity extends BaseActivity<StayThankYouPresenter>
{
    static final int REQUEST_CODE_RECOMMEND_MAP = 10000;
    static final int REQUEST_CODE_DETAIL = 10001;
    static final int REQUEST_CODE_PREVIEW = 10002;

    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_ROOM_NAME = "roomName";
    static final String INTENT_EXTRA_DATA_OVERSEAS = "overseas";
    static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";
    static final String INTENT_EXTRA_DATA_WAITING_FOR_BOOKING = "waitingForBooking";
    static final String INTENT_EXTRA_DATA_LATITUDE = "latitude";
    static final String INTENT_EXTRA_DATA_LONGITUDE = "longitude";
    static final String INTENT_EXTRA_DATA_REWARD_DESCRIPTION_TITLE = "descriptionTitle";
    static final String INTENT_EXTRA_DATA_REWARD_DESCRIPTION_MESSAGE = "descriptionMessage";
    static final String INTENT_EXTRA_DATA_REWARD_WARNING_TEXT_COLOR = "warningTextColor";


    public static Intent newInstance(Context context, boolean overseas, String stayName, String imageUrl//
        , String checkInDateTime, String checkOutDateTime, String roomName, String aggregationId//
        , boolean waitingForBooking, double latitude, double longitude, String descriptionTitle//
        , String descriptionMessage, boolean warningTextColor, StayThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_NAME, roomName);
        intent.putExtra(INTENT_EXTRA_DATA_OVERSEAS, overseas);
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
        intent.putExtra(INTENT_EXTRA_DATA_WAITING_FOR_BOOKING, waitingForBooking);
        intent.putExtra(INTENT_EXTRA_DATA_LATITUDE, latitude);
        intent.putExtra(INTENT_EXTRA_DATA_LONGITUDE, longitude);
        intent.putExtra(INTENT_EXTRA_DATA_REWARD_DESCRIPTION_TITLE, descriptionTitle);
        intent.putExtra(INTENT_EXTRA_DATA_REWARD_DESCRIPTION_MESSAGE, descriptionMessage);
        intent.putExtra(INTENT_EXTRA_DATA_REWARD_WARNING_TEXT_COLOR, warningTextColor);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

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
    protected StayThankYouPresenter createInstancePresenter()
    {
        return new StayThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
