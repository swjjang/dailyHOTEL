package com.daily.dailyhotel.screen.home.stay.outbound.preview;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPreviewActivity extends BaseActivity<StayOutboundPreviewPresenter>
{
    static final int REQUEST_CODE_WISH_DIALOG = 10000;

    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_STAY_NAME = "stayName";
    public static final String INTENT_EXTRA_DATA_STAY_POSITION = "position";
    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";
    public static final String INTENT_EXTRA_DATA_MY_WISH = "myWish";

    /**
     * @param stayIndex
     * @param context
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     * @param numberOfAdults
     * @param childList
     * @return
     */
    public static Intent newInstance(Context context, int stayIndex, int position, String stayName//
        , String checkInDateTime, String checkOutDateTime, int numberOfAdults, ArrayList<Integer> childList)
    {
        Intent intent = new Intent(context, StayOutboundPreviewActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_POSITION, position);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayOutboundPreviewPresenter createInstancePresenter()
    {
        return new StayOutboundPreviewPresenter(this);
    }

    @Override
    public void finish()
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.finish();
    }
}
