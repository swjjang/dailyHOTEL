package com.daily.dailyhotel.screen.home.search.stay.inbound;


import com.daily.base.BaseFragmentDialogView;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayFragmentView extends BaseFragmentDialogView<SearchStayFragmentInterface.OnEventListener, FragmentSearchStayDataBinding>//
    implements SearchStayFragmentInterface.ViewInterface
{
    public SearchStayFragmentView(SearchStayFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchStayDataBinding viewDataBinding)
    {
    }
}
