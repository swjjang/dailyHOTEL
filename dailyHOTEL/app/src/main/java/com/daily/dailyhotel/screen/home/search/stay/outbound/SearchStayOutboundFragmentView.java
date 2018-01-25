package com.daily.dailyhotel.screen.home.search.stay.outbound;


import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.view.DailySearchRecentlyCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayOutboundDataBinding;

import java.util.List;
import java.util.Locale;

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
    public void setPopularAreaList(List<StayOutboundSuggest> popularAreaList)
    {
        if (getViewDataBinding() == null || popularAreaList == null || popularAreaList.size() == 0)
        {
            return;
        }

        final int MAX_COUNT_OF_BEST_AREA = 3;

        getViewDataBinding().popularAreaLayout.removeAllViews();

        int size = popularAreaList.size();

        for (int i = 0; i < size; i++)
        {

            View view = getAreaView(i + 1, popularAreaList.get(i), i < MAX_COUNT_OF_BEST_AREA);

            if (view != null)
            {
                getViewDataBinding().popularAreaLayout.addView(view);
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

    public View getAreaView(int index, StayOutboundSuggest stayOutboundSuggest, boolean bestArea)
    {
        if (stayOutboundSuggest == null)
        {
            return null;
        }

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 48)));
        frameLayout.setTag(stayOutboundSuggest);
        frameLayout.setOnClickListener(v -> getEventListener().onPopularAreaClick((StayOutboundSuggest) v.getTag()));

        // Number
        DailyTextView numberTextView = new DailyTextView(getContext());

        if (bestArea == true)
        {
            numberTextView.setTextColor(getColor(R.color.dh_theme_color));
        } else
        {
            numberTextView.setTextColor(getColor(R.color.default_background_c454545));
        }

        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        numberTextView.setText(String.format(Locale.KOREA, "%d.", index));
        numberTextView.setGravity(Gravity.CENTER_VERTICAL);
        numberTextView.setPadding(ScreenUtils.dpToPx(getContext(), 10), 0, 0, 0);
        frameLayout.addView(numberTextView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Area
        DailyTextView areaTextView = new DailyTextView(getContext());
        areaTextView.setText(stayOutboundSuggest.display);
        areaTextView.setTextColor(getColor(R.color.default_text_c323232));
        areaTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        areaTextView.setGravity(Gravity.CENTER_VERTICAL);
        areaTextView.setPadding(ScreenUtils.dpToPx(getContext(), 45), 0, 0, 0);
        frameLayout.addView(areaTextView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return frameLayout;
    }
}
