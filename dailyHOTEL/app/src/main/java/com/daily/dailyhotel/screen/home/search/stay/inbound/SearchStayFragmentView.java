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

        getViewDataBinding().stayTagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        getViewDataBinding().stayTagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);

        getViewDataBinding().recently01View.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });


        getViewDataBinding().recently02View.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });


        getViewDataBinding().recently03View.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
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

        getViewDataBinding().stayTagFlexboxLayout.removeAllViews();

        for (CampaignTag campaignTag : tagList)
        {
            getViewDataBinding().stayTagFlexboxLayout.addView(getTagView(campaignTag));
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

        getViewDataBinding().stayPopularSearchTagTextView.setVisibility(flag);
        getViewDataBinding().stayTagFlexboxLayout.setVisibility(flag);
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
        final int DP_12 = ScreenUtils.dpToPx(getContext(), 12);
        final int DP_5 = ScreenUtils.dpToPx(getContext(), 5);

        DailyTextView dailyTextView = new DailyTextView(getContext());
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(getColor(R.color.default_text_c323232));
        dailyTextView.setPadding(DP_12, 0, DP_12, 0);
        dailyTextView.setBackgroundResource(R.color.default_background_cf4f4f6);
        dailyTextView.setGravity(Gravity.CENTER_VERTICAL);

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(getContext(), 29));
        layoutParams.setMargins(DP_5, DP_5, DP_5, DP_5);

        dailyTextView.setLayoutParams(layoutParams);
        dailyTextView.setText("#" + campaignTag.campaignTag);
        dailyTextView.setTag(campaignTag);

        return dailyTextView;
    }
}
