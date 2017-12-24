package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.view.ViewGroup;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayTabDataBinding;
import com.twoheart.dailyhotel.databinding.FragmentStayListDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentView extends BaseFragmentDialogView<StayListFragmentView.OnEventListener, FragmentStayListDataBinding> implements StayListFragmentInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayListFragmentView(OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {

    }
}
