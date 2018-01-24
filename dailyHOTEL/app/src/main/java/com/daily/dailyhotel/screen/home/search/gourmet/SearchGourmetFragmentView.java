package com.daily.dailyhotel.screen.home.search.gourmet;


import com.daily.base.BaseFragmentDialogView;
import com.twoheart.dailyhotel.databinding.FragmentSearchGourmetDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetFragmentView extends BaseFragmentDialogView<SearchGourmetFragmentInterface.OnEventListener, FragmentSearchGourmetDataBinding>//
    implements SearchGourmetFragmentInterface.ViewInterface
{
    public SearchGourmetFragmentView(SearchGourmetFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchGourmetDataBinding viewDataBinding)
    {
    }
}
