package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundThankYouActivity extends BaseActivity<StayOutboundThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_IN_TIME = "checkInTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_TIME = "checkOutTime";
    static final String INTENT_EXTRA_DATA_ROOM_TYPE = "roomType";
    static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";
    static final String INTENT_EXTRA_DATA_REWARD_DESCRIPTION_TITLE = "descriptionTitle";
    static final String INTENT_EXTRA_DATA_REWARD_DESCRIPTION_MESSAGE = "descriptionMessage";
    static final String INTENT_EXTRA_DATA_REWARD_WARNING_TEXT_COLOR = "warningTextColor";

    public static Intent newInstance(Context context, String stayName, String imageUrl//
        , String checkInDateTime, String checkOutDateTime, String checkInTime, String checkOutTime//
        , String roomType, String aggregationId, String descriptionTitle, String descriptionMessage//
        , boolean warningTextColor //
        , StayOutboundThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, StayOutboundThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_TIME, checkInTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_TIME, checkOutTime);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE, roomType);
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
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
