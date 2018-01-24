package com.daily.dailyhotel.screen.home.search.stay.outbound;


import com.daily.base.BaseFragmentDialogView;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayOutboundDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundFragmentView extends BaseFragmentDialogView<SearchStayOutboundFragmentInterface.OnEventListener, FragmentSearchStayOutboundDataBinding>//
    implements SearchStayOutboundFragmentInterface.ViewInterface
{
    public SearchStayOutboundFragmentView(SearchStayOutboundFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchStayOutboundDataBinding viewDataBinding)
    {
    }
}
