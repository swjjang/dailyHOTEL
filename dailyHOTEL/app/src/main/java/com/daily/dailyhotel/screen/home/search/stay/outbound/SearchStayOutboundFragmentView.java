package com.daily.dailyhotel.screen.home.search.stay.outbound;


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.view.DailySearchRecentlyCardView;
import com.daily.dailyhotel.view.DailySearchStayOutboundAreaCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayOutboundDataBinding;

import java.util.List;

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
        setRecentlySearchResultVisible(false);
        setPopularAreaVisible(false);

        getViewDataBinding().recently01View.setOnClickListener(v -> getEventListener().onRecentlySearchResultClick((RecentlyDbPlace) v.getTag()));
        getViewDataBinding().recently02View.setOnClickListener(v -> getEventListener().onRecentlySearchResultClick((RecentlyDbPlace) v.getTag()));
        getViewDataBinding().recently03View.setOnClickListener(v -> getEventListener().onRecentlySearchResultClick((RecentlyDbPlace) v.getTag()));

    }

    @Override
    public void setRecentlySearchResultList(List<RecentlyDbPlace> recentlyList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        final int MAX_COUNT = 3;

        DailySearchRecentlyCardView[] recentlyCardView = {getViewDataBinding().recently01View, getViewDataBinding().recently02View, getViewDataBinding().recently03View};

        // 총 3개의 목록만 보여준다
        for (int i = 0; i < MAX_COUNT; i++)
        {
            if (recentlyList != null && recentlyList.size() > i)
            {
                recentlyCardView[i].setVisibility(View.VISIBLE);

                RecentlyDbPlace recentlyDbPlace = recentlyList.get(i);

                recentlyCardView[i].setTag(recentlyDbPlace);
                recentlyCardView[i].setIcon(R.drawable.vector_search_ic_08_history);
                recentlyCardView[i].setNameText(recentlyDbPlace.name);
                recentlyCardView[i].setDateText(null);
                recentlyCardView[i].setOnDeleteClickListener(v -> getEventListener().onRecentlySearchResultDeleteClick(recentlyDbPlace.index));
            } else
            {
                recentlyCardView[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setPopularAreaList(List<StayOutboundSuggest> popularAreaList)
    {
        if (getViewDataBinding() == null || popularAreaList == null || popularAreaList.size() == 0)
        {
            return;
        }

        getViewDataBinding().popularAreaLayout.removeAllViews();

        final int DP_58 = ScreenUtils.dpToPx(getContext(), 58);

        int size = popularAreaList.size();

        for (int i = 0; i < size; i++)
        {
            View view = getAreaView(i + 1, popularAreaList.get(i));

            if (view != null)
            {
                getViewDataBinding().popularAreaLayout.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DP_58));
            }
        }
    }

    @Override
    public void setPopularAreaVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().popularAreaTextView.setVisibility(flag);
        getViewDataBinding().popularAreaLayout.setVisibility(flag);
    }

    @Override
    public void setRecentlySearchResultVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().recentlySearchResultTextView.setVisibility(flag);
        getViewDataBinding().recently01View.setVisibility(flag);
        getViewDataBinding().recently02View.setVisibility(flag);
        getViewDataBinding().recently03View.setVisibility(flag);
    }

    public View getAreaView(int index, StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            return null;
        }

        DailySearchStayOutboundAreaCardView areaCardView = new DailySearchStayOutboundAreaCardView(getContext());

        areaCardView.setTitleText(stayOutboundSuggest.display);
        areaCardView.setSubTitleText(stayOutboundSuggest.country);

        return areaCardView;
    }
}
