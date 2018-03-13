package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;

public interface SearchGourmetResultTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setViewType(SearchGourmetResultTabPresenter.ViewType viewType);
    }

    interface OnEventListener extends OnBaseEventListener
    {
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
