package com.daily.dailyhotel.screen.home.search.stay.inbound;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.daily.base.BaseFragmentDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.view.DailySearchRecentlyCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayFragmentView extends BaseFragmentDialogView<SearchStayFragmentInterface.OnEventListener, FragmentSearchStayDataBinding>//
    implements SearchStayFragmentInterface.ViewInterface
{
    TagAdapter mTagAdapter;

    public SearchStayFragmentView(SearchStayFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchStayDataBinding viewDataBinding)
    {

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
            if (recentlyList != null && recentlyList.size() > 0)
            {
                recentlyCardView[i].setVisibility(View.VISIBLE);

                RecentlyDbPlace recentlyDbPlace = recentlyList.get(i);

                recentlyCardView[i].setIcon(R.drawable.search_ic_01_search);
                recentlyCardView[i].setNameText(recentlyDbPlace.name);
                recentlyCardView[i].setDateText(null);
                recentlyCardView[i].setOnDeleteClickListener(v -> getEventListener().onRecentlySearchResultDeleteClickListener(recentlyDbPlace.index));
            } else
            {
                recentlyCardView[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setPopularSearchTagList(List<String> tagList)
    {
        if (getViewDataBinding() == null || tagList == null || tagList.size() == 0)
        {
            return;
        }

        if (mTagAdapter == null)
        {
            mTagAdapter = new TagAdapter(getContext(), 0);

            getViewDataBinding().stayTagGridView.setAdapter(mTagAdapter);
        }

        mTagAdapter.setData(tagList);
        mTagAdapter.notifyDataSetChanged();
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
        getViewDataBinding().stayTagGridView.setVisibility(flag);
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


    class TagAdapter extends ArrayAdapter<String>
    {
        List<String> mTagList;

        public TagAdapter(@NonNull Context context, int resource)
        {
            super(context, resource);

            mTagList = new ArrayList<>();
        }

        public void setData(List<String> tagList)
        {
            if (tagList == null || tagList.size() == 0)
            {
                return;
            }

            mTagList.clear();
            mTagList.addAll(tagList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            DailyTextView dailyTextView;

            if (convertView == null)
            {
                final int DP_12 = ScreenUtils.dpToPx(parent.getContext(), 12);

                dailyTextView = new DailyTextView(parent.getContext());
                dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                dailyTextView.setTextColor(getColor(R.color.default_text_c666666));
                dailyTextView.setPadding(DP_12, 0, DP_12, 0);
                dailyTextView.setBackgroundResource(R.color.default_background_cf4f4f6);
                dailyTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.dpToPx(parent.getContext(), 30)));
            } else
            {
                dailyTextView = (DailyTextView) convertView;
            }


            dailyTextView.setText(mTagList.get(position));

            return dailyTextView;
        }
    }
}
