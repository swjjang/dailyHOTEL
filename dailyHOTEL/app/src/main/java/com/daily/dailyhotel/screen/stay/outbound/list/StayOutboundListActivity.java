package com.daily.dailyhotel.screen.stay.outbound.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundListActivity extends BaseActivity<StayOutboundListPresenter>
{
    static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    static final String INTENT_EXTRA_DATA_CHECKIN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECKOUT = "checkOut";
    static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";

    static final int REQUEST_CODE_STAYOUTBOUND_DETAIL = 10000;

    /**
     * @param context
     * @param suggest
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     * @param numberOfAdults
     * @param childList
     * @return
     */
    public static Intent newInstance(Context context, Suggest suggest, String checkInDateTime, String checkOutDateTime, int numberOfAdults, ArrayList<String> childList)
    {
        Intent intent = new Intent(context, StayOutboundListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new SuggestParcel(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);
        return intent;
    }

    public static Intent newInstance(Context context, String keyword, String checkInDateTime, String checkOutDateTime, int numberOfAdults, ArrayList<String> childList)
    {
        Intent intent = new Intent(context, StayOutboundListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);
        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayOutboundListActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayOutboundListPresenter createInstancePresenter()
    {
        return new StayOutboundListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
