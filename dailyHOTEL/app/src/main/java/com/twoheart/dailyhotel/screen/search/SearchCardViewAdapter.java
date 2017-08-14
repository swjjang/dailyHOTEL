package com.twoheart.dailyhotel.screen.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SearchCardItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iseung-won on 2017. 8. 10..
 */

public class SearchCardViewAdapter extends RecyclerView.Adapter<SearchCardViewAdapter.CardViewHolder>
{
    public static final int TYPE_STAY = 1;
    public static final int TYPE_GOURMET = 2;

    private static final float CARD_WIDTH_RATIO = 0.772f; // 270/360 = 0.772222222222222;

    private Context mContext;
    private OnEventListener mOnEventListener;

    private ArrayList<SearchCardItem> mRecentlyPlaceList;
    private ArrayList<SearchCardItem> mCampaignTagList;
    private ArrayList<SearchCardItem> mRecentKeywordList;

    private int mType;

    public interface OnEventListener
    {
        void onKeywordDeleteAllClick(int type);

        void onItemClick(View view);
    }

    public SearchCardViewAdapter(Context context, int type //
        , ArrayList<SearchCardItem> placeList, ArrayList<SearchCardItem> campaignTagList //
        , ArrayList<SearchCardItem> keywordList)
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

    public void setOnEventListener(OnEventListener onEventListener)
    {
        mOnEventListener = onEventListener;
    }

    public void setKeywordListData(List<Keyword> keywordList)
    {
        mRecentKeywordList = new ArrayList<>();

        if (keywordList == null)
        {
            return;
        }

        for (Keyword keyword : keywordList)
        {
            SearchCardItem item = new SearchCardItem();
            item.iconType = keyword.icon;
            item.itemText = keyword.name;
            item.object = keyword;
            mRecentKeywordList.add(item);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams((int) getCardWidth(), ViewGroup.LayoutParams.MATCH_PARENT);

        SearchCardItemLayout layout = new SearchCardItemLayout(mContext);

        int horizontalPadding = ScreenUtils.dpToPx(mContext, 6);
        int verticalPadding = ScreenUtils.dpToPx(mContext, 8);

        layout.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        layout.setBackgroundResource(R.drawable.search_card);
        layout.setLayoutParams(params);

        return new CardViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        setCardMargin(holder, position);

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

    public void setCardMargin(CardViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        int inSide = (int) getCardInSideMargin();
        int outSide = (int) getCardOutSideMargin();

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.leftMargin = position == 0 ? outSide : inSide;
        params.rightMargin = position == getItemCount() - 1 ? outSide : inSide;
        holder.itemView.setLayoutParams(params);
    }

    private void onCampaignTagBindViewHolder(CardViewHolder holder, ArrayList<SearchCardItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchCardItemLayout layout = (SearchCardItemLayout) holder.itemView;

        layout.setTitleText(R.string.label_popular_tag);
        layout.setEmptyViewData( //
            TYPE_GOURMET == mType ? R.drawable.no_gourmet_ic : R.drawable.no_hotel_ic //
            , R.string.label_popular_tag);
        layout.setDeleteButtonVisible(false);

        layout.setData(list);

        layout.setOnEventListener(new SearchCardItemLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {
            }

            @Override
            public void onItemClick(View view)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onItemClick(view);
            }

            @Override
            public void onBackClick()
            {
            }
        });
    }

    private void onRecentKeywordBindViewHolder(CardViewHolder holder, ArrayList<SearchCardItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchCardItemLayout layout = (SearchCardItemLayout) holder.itemView;

        layout.setTitleText(R.string.label_search_recentsearches);
        layout.setEmptyViewData( //
            TYPE_GOURMET == mType ? R.drawable.no_gourmet_ic : R.drawable.no_hotel_ic //
            , R.string.label_search_recentsearches);
        layout.setDeleteButtonVisible(true);

        layout.setData(list);

        layout.setOnEventListener(new SearchCardItemLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onKeywordDeleteAllClick(mType);
            }

            @Override
            public void onItemClick(View view)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onItemClick(view);
            }

            @Override
            public void onBackClick()
            {
            }
        });
    }

    private void onRecentlyPlaceBindViewHolder(CardViewHolder holder, ArrayList<SearchCardItem> list)
    {
        if (holder == null)
        {
            return;
        }

        SearchCardItemLayout layout = (SearchCardItemLayout) holder.itemView;

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

        layout.setEmptyViewData( //
            TYPE_GOURMET == mType ? R.drawable.no_gourmet_ic : R.drawable.no_hotel_ic //
            , titleResId);

        layout.setDeleteButtonVisible(false);

        layout.setData(list);

        layout.setOnEventListener(new SearchCardItemLayout.OnEventListener()
        {
            @Override
            public void onDeleteAllClick()
            {
            }

            @Override
            public void onItemClick(View view)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onItemClick(view);
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

    private float getCardWidth()
    {
        return ScreenUtils.getScreenWidth(mContext) * CARD_WIDTH_RATIO;
    }

    private float getCardOutSideMargin()
    {
        return ScreenUtils.getScreenWidth(mContext) * (1.0f - CARD_WIDTH_RATIO) / 2.0f;
    }

    private float getCardInSideMargin()
    {
        return 0.0f - ScreenUtils.dpToPx(mContext, 1d); // -2dp 만큼 곂침으로 양쪽에 1만큼씩 여백 줄이면 됨
    }

    public class CardViewHolder extends RecyclerView.ViewHolder
    {
        public CardViewHolder(View view)
        {
            super(view);
        }
    }
}
