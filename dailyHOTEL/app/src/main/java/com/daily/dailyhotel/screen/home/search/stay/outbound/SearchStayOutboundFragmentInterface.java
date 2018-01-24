package com.daily.dailyhotel.screen.home.search.stay.outbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayOutboundFragmentInterface
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
