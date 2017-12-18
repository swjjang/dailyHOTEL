package com.daily.dailyhotel.screen.mydaily.reward.history.card;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.dailyhotel.entity.RewardCardHistory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRewardCardHistoryDataBinding;

import java.util.ArrayList;
import java.util.List;

public class RewardCardHistoryAdapter extends RecyclerView.Adapter<RewardCardHistoryAdapter.CardHistoryViewHolder>
{
    Context mContext;
    private List<RewardCardHistory> mList;
    OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onClick(View view);
    }

    public RewardCardHistoryAdapter(Context context)
    {
        mContext = context;

        mList = new ArrayList<>();
    }

    public void setOnClickListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }

    public void clear()
    {
        mList.clear();
    }

    public void addAll(List<RewardCardHistory> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(List<RewardCardHistory> collection)
    {
        clear();
        addAll(collection);
    }

    public void remove(int position)
    {
        if (mList == null || mList.size() <= position)
        {
            return;
        }

        mList.remove(position);
    }

    public RewardCardHistory getItem(int position)
    {
        if (position < 0 || mList.size() <= position)
        {
            return null;
        }

        return mList.get(position);
    }

    @Override
    public int getItemCount()
    {
        if (mList == null)
        {
            return 0;
        }

        return mList.size();
    }

    @Override
    public RewardCardHistoryAdapter.CardHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutRewardCardHistoryDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_card_history_data, parent, false);

        return new CardHistoryViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(RewardCardHistoryAdapter.CardHistoryViewHolder holder, int position)
    {
        final int MAX_COUNT = 9;

        RewardCardHistory rewardCardHistory = getItem(position);
        List<String> rewardCartHistoryList = rewardCardHistory.getStickerTypeList();

        for(int i = 0; i < MAX_COUNT; i++)
        {
            switch (rewardCartHistoryList.get(i))
            {
                case "EVENT":
                    holder.dataBinding.sticker1nightsImageView;
                    holder.stickerViews[i].setVisibility(View.VISIBLE);
                    holder.stickerViews[i].setImageResource(R.drawable.r_ic_l_47_shadow_event);
                    break;


                case "REWARD":
                    break;
            }
        }
    }

    class CardHistoryViewHolder extends RecyclerView.ViewHolder
    {
        LayoutRewardCardHistoryDataBinding dataBinding;
        final ImageView[] stickerViews;

        public CardHistoryViewHolder(LayoutRewardCardHistoryDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            stickerViews = new ImageView[]{dataBinding.sticker1nightsImageView//
                , dataBinding.sticker2nightsImageView//
                , dataBinding.sticker3nightsImageView//
                , dataBinding.sticker4nightsImageView//
                , dataBinding.sticker5nightsImageView//
                , dataBinding.sticker6nightsImageView//
                , dataBinding.sticker7nightsImageView//
                , dataBinding.sticker8nightsImageView//
                , dataBinding.sticker9nightsImageView};
        }
    }
}
