package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StayOutboundDetailView extends BaseView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding> implements StayOutboundDetailViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundDetailView(BaseActivity baseActivity, StayOutboundDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }


    @Override
    public void onClick(View v)
    {

    }
}
