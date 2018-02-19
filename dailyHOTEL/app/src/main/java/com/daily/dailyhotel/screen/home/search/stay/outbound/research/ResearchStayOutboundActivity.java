package com.daily.dailyhotel.screen.home.search.stay.outbound.research;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchStayOutboundActivity extends BaseActivity<ResearchStayOutboundPresenter>
{
    static final int REQUEST_CODE_SUGGEST = 10000;
    static final int REQUEST_CODE_CALENDAR = 10001;
    static final int REQUEST_CODE_PEOPLE = 10002;
    static final int REQUEST_CODE_DETAIL = 10003;

    static final String INTENT_EXTRA_DATA_OPEN_DATE_TIME = "openDateTime";
    static final String INTENT_EXTRA_DATA_CLOSE_DATE_TIME = "closeDateTime";
    static final String INTENT_EXTRA_DATA_CURRENT_DATE_TIME = "currentDateTime";
    static final String INTENT_EXTRA_DATA_DAILY_DATE_TIME = "dailyDateTime";

    public static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    public static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    public static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    public static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    public static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    public static final String INTENT_EXTRA_DATA_CLICK_TYPE = "clickType";

    public static Intent newInstance(Context context, String openDateTime, String closeDateTime, String currentDateTime, String dailyDateTime//
        , String checkInDateTime, String checkOutDateTime, StayOutboundSuggest suggest, int numberOfAdults, ArrayList<Integer> childList)
    {
        Intent intent = new Intent(context, ResearchStayOutboundActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_OPEN_DATE_TIME, openDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CLOSE_DATE_TIME, closeDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CURRENT_DATE_TIME, currentDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_DAILY_DATE_TIME, dailyDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new StayOutboundSuggestParcel(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected ResearchStayOutboundPresenter createInstancePresenter()
    {
        return new ResearchStayOutboundPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
