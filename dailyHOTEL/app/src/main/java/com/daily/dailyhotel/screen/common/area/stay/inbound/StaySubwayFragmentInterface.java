package com.daily.dailyhotel.screen.common.area.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface StaySubwayFragmentInterface
{
    interface ViewInterface extends BaseFragmentDialogViewInterface
    {
    }

    interface OnEventListener extends OnBaseEventListener
    {
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
