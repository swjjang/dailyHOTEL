package com.daily.dailyhotel.screen.home.search.stay.inbound;


import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.view.DailySearchRecentlyCardView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayDataBinding;

import java.util.List;

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
        setRecentlySearchResultVisible(false);
        setPopularSearchTagVisible(false);

        getViewDataBinding().tagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        getViewDataBinding().tagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);

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
                recentlyCardView[i].setIcon(R.drawable.search_ic_01_search);
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
    public void setPopularSearchTagList(List<CampaignTag> tagList)
    {
        if (getViewDataBinding() == null || tagList == null || tagList.size() == 0)
        {
            return;
        }

        getViewDataBinding().tagFlexboxLayout.removeAllViews();

        for (CampaignTag campaignTag : tagList)
        {
            View view = getTagView(campaignTag);

            if (view != null)
            {
                getViewDataBinding().tagFlexboxLayout.addView(view);
            }
        }
    }

    @Override
    public void setPopularSearchTagVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().popularSearchTagTextView.setVisibility(flag);
        getViewDataBinding().tagFlexboxLayout.setVisibility(flag);
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

    private View getTagView(CampaignTag campaignTag)
    {
        if (campaignTag == null)
        {
            return null;
        }

        final int DP_12 = ScreenUtils.dpToPx(getContext(), 12);
        final int DP_5 = ScreenUtils.dpToPx(getContext(), 5);

        DailyTextView dailyTextView = new DailyTextView(getContext());
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(getColor(R.color.default_text_c666666));
        dailyTextView.setPadding(DP_12, 0, DP_12, 0);
        dailyTextView.setBackgroundResource(R.drawable.shape_fillrect_le7e7e7_bffffff_r50);
        dailyTextView.setGravity(Gravity.CENTER_VERTICAL);

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(getContext(), 29));
        layoutParams.setMargins(DP_5, DP_5, DP_5, DP_5);

        dailyTextView.setLayoutParams(layoutParams);
        dailyTextView.setText("#" + campaignTag.campaignTag);
        dailyTextView.setTag(campaignTag);
        dailyTextView.setOnClickListener(v -> getEventListener().onPopularTagClick((CampaignTag) v.getTag()));

        return dailyTextView;
    }
}
