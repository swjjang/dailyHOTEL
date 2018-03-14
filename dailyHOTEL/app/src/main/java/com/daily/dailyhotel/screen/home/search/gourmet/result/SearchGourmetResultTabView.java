package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.databinding.ActivitySearchGourmetResultTabDataBinding;

public class SearchGourmetResultTabView extends BaseDialogView<SearchGourmetResultTabInterface.OnEventListener, ActivitySearchGourmetResultTabDataBinding> implements SearchGourmetResultTabInterface.ViewInterface
{
    public SearchGourmetResultTabView(BaseActivity baseActivity, SearchGourmetResultTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {

            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {

            }
        });
    }

    @Override
    public void setViewType(SearchGourmetResultTabPresenter.ViewType viewType)
    {

    }

    @Override
    public void setToolbarDateText(String text)
    {
        getViewDataBinding().toolbarView.setSubTitleText(text);
    }
}
