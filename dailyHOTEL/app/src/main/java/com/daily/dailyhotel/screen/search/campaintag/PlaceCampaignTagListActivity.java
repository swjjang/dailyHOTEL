package com.daily.dailyhotel.screen.search.campaintag;


import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public abstract class PlaceCampaignTagListActivity extends
    BaseActivity<PlaceCampaignTagListPresenter>
{

    protected abstract PlaceCampaignTagListPresenter getPlaceCampaignTagListPresenter();

    @NonNull
    @Override
    protected PlaceCampaignTagListPresenter createInstancePresenter()
    {
        return getPlaceCampaignTagListPresenter();
    }
}
