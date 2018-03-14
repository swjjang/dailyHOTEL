package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SearchGourmetResultTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setViewType(SearchGourmetResultTabPresenter.ViewType viewType);

        void setToolbarDateText(String text);

        void setFloatingActionViewVisible(boolean visible);

        Observable<BasePagerFragment> setCampaignTagFragment();

        Observable<Boolean> setSearchResultFragment();
    }

    interface OnEventListener extends OnBaseEventListener
    {
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
