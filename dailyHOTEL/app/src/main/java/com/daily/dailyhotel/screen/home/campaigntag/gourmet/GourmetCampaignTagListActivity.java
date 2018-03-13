package com.daily.dailyhotel.screen.home.campaigntag.gourmet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class GourmetCampaignTagListActivity extends BaseActivity<GourmetCampaignTagListPresenter>
{
    static final int REQUEST_CODE_WISH_DIALOG = 10000;
    static final int REQUEST_CODE_RESEARCH = 10001;
    protected static final int REQUEST_CODE_CALL = 10000;

    protected static final String INTENT_EXTRA_DATA_INDEX = "index";
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";


    public static Intent newInstance(Context context, int index, String hashTag, String visitDate)
    {
        Intent intent = new Intent(context, GourmetCampaignTagListActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, hashTag);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, visitDate);

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
    protected GourmetCampaignTagListPresenter createInstancePresenter()
    {
        return new GourmetCampaignTagListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
