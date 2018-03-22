package com.daily.dailyhotel.screen.home.search.stay.inbound.result;

import android.app.Activity;
import android.support.design.widget.TabLayout;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;

import java.util.List;

import io.reactivex.Observable;

public interface SearchStayResultTabInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setViewType(SearchStayResultTabPresenter.ViewType viewType);

        void setToolbarTitleImageResource(int resId);

        void setToolbarDateText(String text);

        void setToolbarRadiusSpinnerVisible(boolean visible);

        void setRadiusSpinnerSelection(float radius);

        void setFloatingActionViewVisible(boolean visible);

        void setOptionFilterSelected(boolean selected);

        Observable<BasePagerFragment> setCampaignTagFragment();

        Observable<BasePagerFragment> setSearchResultFragment(String callByScreen);

        void setEmptyViewVisible(boolean visible);

        void setEmptyViewCampaignTagVisible(boolean visible);

        void setEmptyViewCampaignTag(String title, List<CampaignTag> campaignTagList);

        boolean onFragmentBackPressed();

        void refreshCurrentFragment();

        void removeAllFragment();

        void setCategoryVisible(boolean visible);

        void setCategoryTabSelect(int position);

        void onSelectedCategory();

        void refreshCurrentCategory();

        void scrollTopCurrentCategory();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onToolbarTitleClick();

        void onResearchClick();

        void onEmptyStayResearchClick();

        void onFinishAndRefresh();

        void onViewTypeClick();

        void onFilterClick();

        void onCalendarClick();

        void onChangedRadius(float radius);

        void setEmptyViewVisible(boolean visible);

        void onGourmetClick();

        void onStayOutboundClick();

        void onCampaignTagClick(CampaignTag campaignTag);

        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(SearchStayResultAnalyticsParam analyticsParam);

        SearchStayResultAnalyticsParam getAnalyticsParam();

        void onEventChangedViewType(Activity activity, SearchStayResultTabPresenter.ViewType viewType);

        void onEventCalendarClick(Activity activity);

        void onEventFilterClick(Activity activity);

        void onEventBackClick(Activity activity, boolean locationSuggestType);

        void onEventCancelClick(Activity activity, boolean locationSuggestType);

        void onEventResearchClick(Activity activity, StaySuggestV2 suggest);

        void onEventChangedRadius(Activity activity, StaySuggestV2 suggest, float radius);

        void onEventGourmetClick(Activity activity);

        void onEventStayOutboundClick(Activity activity);

        void onEventCampaignTagClick(Activity activity, int index);
    }
}
