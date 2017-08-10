package com.twoheart.dailyhotel.screen.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutSearchOptionItemListBinding;
import com.twoheart.dailyhotel.model.SearchOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2017. 8. 10..
 */

public class PlaceSearchRecyclerAdapter extends RecyclerView.Adapter<PlaceSearchRecyclerAdapter.OptionViewHolder>
{
    public static final int TYPE_STAY = 1;
    public static final int TYPE_GOURMET = 2;

    private Context mContext;
    //    private ArrayList<Stay> mRecentlyPlaceList;
    //    private ArrayList<CampaignTag> mCampaignTagList;
    //    private ArrayList<Keyword> mRecentKeywordList;

    private ArrayList<SearchOptionItem> mRecentlyPlaceList;
    private ArrayList<SearchOptionItem> mCampaignTagList;
    private ArrayList<SearchOptionItem> mRecentKeywordList;

    private int mType;


    public PlaceSearchRecyclerAdapter(Context context, int type //
        , ArrayList<SearchOptionItem> placeList, ArrayList<SearchOptionItem> campaignTagList //
        , ArrayList<SearchOptionItem> keywordList)
    {
        mContext = context;

        mType = type;

        mRecentlyPlaceList = new ArrayList<>();
        mCampaignTagList = new ArrayList<>();
        mRecentKeywordList = new ArrayList<>();

        if (placeList != null && placeList.size() > 0)
        {
            mRecentlyPlaceList.addAll(placeList);
        }

        if (campaignTagList != null && campaignTagList.size() > 0)
        {
            mCampaignTagList.addAll(campaignTagList);
        }

        if (keywordList != null && keywordList.size() > 0)
        {
            mRecentKeywordList.addAll(keywordList);
        }
    }

    @Override
    public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //        LayoutSearchOptionItemListBinding dataBinding = DataBindingUtil.inflate( //
        //            LayoutInflater.from(mContext), R.layout.layout_search_option_item_list, parent, false);

        SearchOptionItemListLayout layout = new SearchOptionItemListLayout(mContext);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        int horizontalPadding = ScreenUtils.dpToPx(mContext, 6);
        int verticalPadding = ScreenUtils.dpToPx(mContext, 8);
        layout.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        layout.setBackgroundResource(R.drawable.search_card);

        return new OptionViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(OptionViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        switch (position)
        {
            case 0:
                onCampaignTagBindViewHolder(holder, mCampaignTagList);
                break;

            case 1:
                onRecentKeywordBindViewHolder(holder, mRecentKeywordList);
                break;

            case 2:
                onRecentlyPlaceBindViewHolder(holder, mRecentlyPlaceList);
                break;
        }
    }

    private void onCampaignTagBindViewHolder(OptionViewHolder holder, ArrayList<SearchOptionItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchOptionItemListLayout layout = (SearchOptionItemListLayout) holder.itemView;

        layout.setTitleText(R.string.label_popular_tag);
        layout.setDeleteButtonVisible(false);

        layout.setData(list);

        layout.setOnEventListener(new SearchOptionItemListLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {

            }

            @Override
            public void onItemClick(View view)
            {

            }

            @Override
            public void onBackClick()
            {

            }
        });
    }

    private void onRecentKeywordBindViewHolder(OptionViewHolder holder, ArrayList<SearchOptionItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchOptionItemListLayout layout = (SearchOptionItemListLayout) holder.itemView;

        layout.setTitleText(R.string.label_search_recentsearches);
        layout.setDeleteButtonVisible(true);

        layout.setData(list);

        layout.setOnEventListener(new SearchOptionItemListLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {

            }

            @Override
            public void onItemClick(View view)
            {

            }

            @Override
            public void onBackClick()
            {

            }
        });
    }

    private void onRecentlyPlaceBindViewHolder(OptionViewHolder holder, ArrayList<SearchOptionItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchOptionItemListLayout layout = (SearchOptionItemListLayout) holder.itemView;

        int titleResId = 0;
        if (TYPE_STAY == mType)
        {
            titleResId = R.string.label_recently_stay;
        } else if (TYPE_GOURMET == mType)
        {
            titleResId = R.string.label_recently_gourmet;
        }

        if (titleResId == 0)
        {
            layout.setTitleText("");
        } else
        {
            layout.setTitleText(titleResId);
        }

        layout.setDeleteButtonVisible(false);

        layout.setData(list);

        layout.setOnEventListener(new SearchOptionItemListLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {

            }

            @Override
            public void onItemClick(View view)
            {

            }

            @Override
            public void onBackClick()
            {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        // 항상 3개입니다 리스트(뷰) 가 항상 3개(최근 본, 최근 검색, 캠페인 태그)
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 1;
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder
    {
        public OptionViewHolder(View view)
        {
            super(view);
        }
    }
}
