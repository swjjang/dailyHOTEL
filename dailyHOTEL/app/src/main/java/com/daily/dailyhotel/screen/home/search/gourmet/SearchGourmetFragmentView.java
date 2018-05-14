package com.daily.dailyhotel.screen.home.search.gourmet;


import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.view.DailySearchRecentlyCardView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchGourmetDataBinding;

import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetFragmentView extends BaseFragmentDialogView<SearchGourmetFragmentInterface.OnEventListener, FragmentSearchGourmetDataBinding>//
    implements SearchGourmetFragmentInterface.ViewInterface
{
    public SearchGourmetFragmentView(SearchGourmetFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchGourmetDataBinding viewDataBinding)
    {
        setRecentlyHistoryVisible(false);
        setPopularSearchTagVisible(false);

        getViewDataBinding().tagFlexboxLayout.setFlexDirection(FlexDirection.ROW);
        getViewDataBinding().tagFlexboxLayout.setFlexWrap(FlexWrap.WRAP);

        getViewDataBinding().recently01View.setOnClickListener(v -> getEventListener().onRecentlyHistoryClick((GourmetSearchResultHistory) v.getTag()));
        getViewDataBinding().recently02View.setOnClickListener(v -> getEventListener().onRecentlyHistoryClick((GourmetSearchResultHistory) v.getTag()));
        getViewDataBinding().recently03View.setOnClickListener(v -> getEventListener().onRecentlyHistoryClick((GourmetSearchResultHistory) v.getTag()));
    }

    @Override
    public void setRecentlyHistory(List<GourmetSearchResultHistory> recentlyHistoryList)
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
            if (recentlyHistoryList != null && recentlyHistoryList.size() > i)
            {
                recentlyCardView[i].setVisibility(View.VISIBLE);
                recentlyCardView[i].setBackgroundResource(R.drawable.selector_background_drawable_cf8f8f9_cffffff);

                GourmetSearchResultHistory recentlyHistory = recentlyHistoryList.get(i);
                GourmetSuggest suggest = recentlyHistory.gourmetSuggest;

                recentlyCardView[i].setTag(recentlyHistory);
                recentlyCardView[i].setIcon(R.drawable.vector_search_ic_08_history);

                if (suggest.isLocationSuggestType() == true)
                {
                    recentlyCardView[i].setNameText(getString(R.string.label_search_suggest_type_location_item_format, suggest.getText1()));
                } else
                {
                    recentlyCardView[i].setNameText(suggest.getText1());
                }

                recentlyCardView[i].setDateText(recentlyHistory.gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));
                recentlyCardView[i].setOnDeleteClickListener(v -> getEventListener().onRecentlyHistoryDeleteClick(recentlyHistory));
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

        if (getViewDataBinding().tagFlexboxLayout.getChildCount() > 0)
        {
            getViewDataBinding().tagFlexboxLayout.removeAllViews();
        }

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
    public void setRecentlyHistoryVisible(boolean visible)
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

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getViewDataBinding().popularSearchTagTextView.getLayoutParams();
        layoutParams.topMargin = visible ? ScreenUtils.dpToPx(getContext(), 15) : 0;
        getViewDataBinding().popularSearchTagTextView.setLayoutParams(layoutParams);
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
        dailyTextView.setMaxLines(1);
        dailyTextView.setSingleLine();

        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(getContext(), 29));
        layoutParams.setMargins(DP_5, DP_5, DP_5, DP_5);

        dailyTextView.setLayoutParams(layoutParams);
        dailyTextView.setText("#" + campaignTag.campaignTag);
        dailyTextView.setTag(campaignTag);
        dailyTextView.setOnClickListener(v -> getEventListener().onPopularTagClick((CampaignTag) v.getTag()));

        return dailyTextView;
    }
}
