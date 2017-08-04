package com.daily.dailyhotel.screen.home.campaintag;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityPlaceCampainTagListDataBinding;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class PlaceCampaignTagListView extends BaseDialogView<PlaceCampaignTagListView
    .OnEventListener, ActivityPlaceCampainTagListDataBinding>
    implements PlaceCampaignTagListInterface
{
    public PlaceCampaignTagListView(BaseActivity activity, OnEventListener listener)
    {
        super(activity, listener);
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    @Override
    protected void setContentView(ActivityPlaceCampainTagListDataBinding viewDataBinding)
    {

    }

    public interface OnEventListener extends OnBaseEventListener
    {

    }


}
