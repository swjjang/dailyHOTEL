package com.daily.dailyhotel.screen.common.area.stay;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */

@Deprecated
public class StayAreaListActivity extends BaseActivity<StayAreaListPresenter>
{
    static final int REQUEST_CODE_SETTING_LOCATION = 10000;
    static final int REQUEST_CODE_PERMISSION_MANAGER = 10001;
    static final int REQUEST_CODE_SEARCH = 10002;

    static final String INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME = "checkInDateTime";
    static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME = "checkOutDateTime";
    public static final String INTENT_EXTRA_DATA_STAY_CATEGORY = "stayCategory";
    static final String INTENT_EXTRA_DATA_CATEGORY_CODE = "categoryCode";
    public static final String INTENT_EXTRA_DATA_REGION = "area";
    public static final String INTENT_EXTRA_DATA_CHANGED_AREA_GROUP = "areaGroup";

    //    public static Intent newInstance(Context context, String checkInDateTime, String checkOutDateTime, DailyCategoryType dailyCategoryType, String categoryCode)
    //    {
    //        Intent intent = new Intent(context, StayAreaListActivity.class);
    //        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
    //        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
    //        intent.putExtra(INTENT_EXTRA_DATA_STAY_CATEGORY, dailyCategoryType.name());
    //        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY_CODE, categoryCode);
    //
    //        return intent;
    //    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayAreaListPresenter createInstancePresenter()
    {
        return new StayAreaListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
