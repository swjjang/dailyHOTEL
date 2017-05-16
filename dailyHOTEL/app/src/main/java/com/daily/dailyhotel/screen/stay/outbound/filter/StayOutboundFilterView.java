package com.daily.dailyhotel.screen.stay.outbound.filter;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundFilterDataBinding;

public class StayOutboundFilterView extends BaseView<StayOutboundFilterView.OnEventListener, ActivityStayOutboundFilterDataBinding> implements StayOutboundFilterViewInterface, View.OnClickListener
{


    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundFilterView(BaseActivity baseActivity, StayOutboundFilterView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void onClick(View v)
    {

    }
}
