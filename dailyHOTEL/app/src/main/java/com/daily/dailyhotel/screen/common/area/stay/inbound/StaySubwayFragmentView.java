package com.daily.dailyhotel.screen.common.area.stay.inbound;


import com.daily.base.BaseFragmentDialogView;
import com.twoheart.dailyhotel.databinding.FragmentStayListDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StaySubwayFragmentView extends BaseFragmentDialogView<StaySubwayFragmentInterface.OnEventListener, FragmentStayListDataBinding>//
    implements StaySubwayFragmentInterface.ViewInterface
{
    public StaySubwayFragmentView(StaySubwayFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentStayListDataBinding viewDataBinding)
    {
    }
}
