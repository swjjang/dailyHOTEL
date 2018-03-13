package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class SearchGourmetResultTabView extends BaseDialogView<SearchGourmetResultTabInterface.OnEventListener, ActivityCopyDataBinding> implements SearchGourmetResultTabInterface.ViewInterface
{
    public SearchGourmetResultTabView(BaseActivity baseActivity, SearchGourmetResultTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCopyDataBinding viewDataBinding)
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

    private void initToolbar(ActivityCopyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void setViewType(SearchGourmetResultTabPresenter.ViewType viewType)
    {

    }
}
