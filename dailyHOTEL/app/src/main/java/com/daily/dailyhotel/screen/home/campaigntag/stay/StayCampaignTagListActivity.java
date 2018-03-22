package com.daily.dailyhotel.screen.home.campaigntag.stay;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 8. 4..
 */

@Deprecated
public class StayCampaignTagListActivity extends BaseActivity<StayCampaignTagListPresenter>
{
    static final int REQUEST_CODE_WISH_DIALOG = 10000;

    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_CHECK_IN_DATE = "checkInDate";
    protected static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE = "checkOutDate";

    protected static final int REQUEST_CODE_CALL = 10000;

    //    public static Intent newInstance(Context context, int index, String hashTag, String checkInDate, String checkOutDate)
    //    {
    //        Intent intent = new Intent(context, StayCampaignTagListActivity.class);
    //
    //        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
    //        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
    //        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, checkInDate);
    //        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE, checkOutDate);
    //
    //        return intent;
    //    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected StayCampaignTagListPresenter createInstancePresenter()
    {
        return new StayCampaignTagListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
