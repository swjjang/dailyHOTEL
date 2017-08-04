package com.daily.dailyhotel.screen.home.campaintag;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public abstract class PlaceCampaignTagListPresenter extends
    BaseExceptionPresenter<PlaceCampaignTagListActivity, PlaceCampaignTagListInterface>
    implements PlaceCampaignTagListView.OnEventListener
{

    protected abstract PlaceCampaignTagListView getCampaignTagListView();

    public PlaceCampaignTagListPresenter(@NonNull PlaceCampaignTagListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected PlaceCampaignTagListInterface createInstanceViewInterface()
    {
        return getCampaignTagListView();
    }

    @Override
    public void constructorInitialize(PlaceCampaignTagListActivity activity)
    {
        setContentView(R.layout.activity_place_campain_tag_list_data);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onRefresh(boolean showProgress)
    {

    }



    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {

    }

    @Override
    public void onPostCreate()
    {

    }


    @Override
    public void onBackClick()
    {

    }
}
