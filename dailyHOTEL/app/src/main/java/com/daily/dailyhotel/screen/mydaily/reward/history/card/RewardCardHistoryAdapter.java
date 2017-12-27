package com.daily.dailyhotel.screen.mydaily.reward.history.card;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.RewardCardHistory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRewardCardHistoryDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRewardCardHistoryFooterDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.ArrayList;
import java.util.List;

public class RewardCardHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mList;
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

    public void addAll(List<ObjectItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(List<ObjectItem> collection)
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

    public ObjectItem getItem(int position)
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
    public int getItemViewType(int position)
    {
        return mList.get(position).mType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_ENTRY:
            {
                LayoutRewardCardHistoryDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_card_history_data, parent, false);

                return new CardHistoryViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutRewardCardHistoryFooterDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_card_history_footer_data, parent, false);

                return new BaseDataBindingViewHolder(dataBinding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        switch (getItemViewType(position))
        {
            case ObjectItem.TYPE_ENTRY:
                onBindViewHolder((CardHistoryViewHolder) holder, position);
                break;

            case ObjectItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    private void onBindViewHolder(CardHistoryViewHolder holder, int position)
    {
        RewardCardHistory rewardCardHistory = getItem(position).getItem();

        if (position == 0)
        {
            holder.dataBinding.getRoot().setPadding(0, ScreenUtils.dpToPx(mContext, 6), 0, 0);
        } else
        {
            holder.dataBinding.getRoot().setPadding(0, 0, 0, 0);
        }

        // 리워드
        holder.dataBinding.cardTitleTextView.setText(DailyTextUtils.isTextEmpty(rewardCardHistory.rewardCouponPublishedAtDateTime) ? R.string.label_reward_card_history_coupon_will_be_issued : R.string.label_reward_card_history_coupon_issued);

        List<String> rewardCartHistoryList = rewardCardHistory.getStickerTypeList();

        for (int i = 0; i < rewardCardHistory.rewardStickerCount; i++)
        {
            switch (rewardCartHistoryList.get(i))
            {
                case "E":
                    holder.stickerViews[i].setImageResource(R.drawable.r_ic_l_47_shadow_event);
                    break;


                case "R":
                    holder.stickerViews[i].setImageResource(R.drawable.r_ic_l_47_shadow);
                    break;
            }
        }

        // 카드 시작일
        if (DailyTextUtils.isTextEmpty(rewardCardHistory.createdAtDateTime) == true)
        {
            holder.dataBinding.cardCreatedDayTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.cardCreatedDayTextView.setVisibility(View.VISIBLE);

            try
            {
                holder.dataBinding.cardCreatedDayTextView.setText(mContext.getString(R.string.label_reward_card_history_coupon_started_date//
                    , DailyCalendar.convertDateFormatString(rewardCardHistory.createdAtDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd HH:mm")));
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                holder.dataBinding.cardCreatedDayTextView.setVisibility(View.GONE);
            }
        }

        // 쿠폰 발행일
        if (DailyTextUtils.isTextEmpty(rewardCardHistory.rewardCouponPublishedAtDateTime) == true)
        {
            holder.dataBinding.couponIssuedDayTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.couponIssuedDayTextView.setVisibility(View.VISIBLE);

            try
            {
                holder.dataBinding.couponIssuedDayTextView.setText(mContext.getString(R.string.label_reward_card_history_coupon_issued_date//
                    , DailyCalendar.convertDateFormatString(rewardCardHistory.rewardCouponPublishedAtDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")));
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                holder.dataBinding.couponIssuedDayTextView.setVisibility(View.GONE);
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

    protected class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
