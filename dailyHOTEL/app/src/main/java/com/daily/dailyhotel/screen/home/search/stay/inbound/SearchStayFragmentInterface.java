package com.daily.dailyhotel.screen.home.search.stay.inbound;


import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragmentDialogViewInterface;
import com.daily.base.OnBaseEventListener;

/**
 * Created by sheldon
 * Clean Architecture
 */
public interface SearchStayFragmentInterface
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
