package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.view.ViewGroup;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayTabDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseFragmentDialogView<StayListFragmentView.OnEventListener, ActivityStayTabDataBinding>
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(ActivityStayTabDataBinding viewDataBinding)
    {

    }
}
