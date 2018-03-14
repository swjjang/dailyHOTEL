package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;

import java.util.List;

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

        void setEmptyViewVisible(boolean visible);

        void setEmptyViewCampaignTagVisible(boolean visible);

        void setEmptyViewCampaignTag(String title, List<CampaignTag> campaignTagList);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onResearchClick();

        void onFinishAndRefresh();

        void onChangedRadius(float radius);

        void setEmptyViewVisible(boolean visible);

        void onStayClick();

        void onStayOutboundClick();

        void onCampaignTagClick(CampaignTag campaignTag);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
