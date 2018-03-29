package com.daily.dailyhotel.screen.mydaily.reward.history.reward;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.RewardHistory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRewardHistoryDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRewardHistoryFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRewardHistoryHeaderDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RewardHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    private List<ObjectItem> mList;
    OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onClick(View view);
    }

    public RewardHistoryAdapter(Context context)
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

    public void add(ObjectItem objectItem)
    {
        mList.add(objectItem);
    }

    public void add(int position, ObjectItem placeViewItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends ObjectItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mList.addAll(collection);
    }

    public void setAll(Collection<? extends ObjectItem> collection)
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
    public int getItemViewType(int position)
    {
        return mList.get(position).mType;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_HEADER_VIEW:
            {
                LayoutRewardHistoryHeaderDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_history_header_data, parent, false);

                return new BaseDataBindingViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_ENTRY:
            {
                LayoutRewardHistoryDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_history_data, parent, false);

                return new HistoryViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutRewardHistoryFooterDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_reward_history_footer_data, parent, false);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ObjectItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case ObjectItem.TYPE_ENTRY:
                onBindViewHolder((HistoryViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(HistoryViewHolder holder, ObjectItem objectItem)
    {
        if (holder == null || objectItem == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        RewardHistory rewardHistory = objectItem.getItem();

        switch (rewardHistory.type)
        {
            case CREATED_STICKER:
                switch (rewardHistory.rewardStickerType)
                {
                    case "R":
                    {
                        final int DP_16 = ScreenUtils.dpToPx(mContext, 16);
                        final int DP_18 = ScreenUtils.dpToPx(mContext, 18);
                        holder.dataBinding.descriptionLayout.setPadding(0, DP_16, 0, DP_18);

                        final int[] RESOURCE_NIGHT = {R.drawable.vector_ic_reward_history_night_1//
                            , R.drawable.vector_ic_reward_history_night_2//
                            , R.drawable.vector_ic_reward_history_night_3//
                            , R.drawable.vector_ic_reward_history_night_4//
                            , R.drawable.vector_ic_reward_history_night_5//
                            , R.drawable.vector_ic_reward_history_night_6//
                            , R.drawable.vector_ic_reward_history_night_7//
                            , R.drawable.vector_ic_reward_history_night_8//
                            , R.drawable.vector_ic_reward_history_night_9};

                        holder.dataBinding.rewardImageView.setVectorImageResource(RESOURCE_NIGHT[rewardHistory.nights - 1]);

                        holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
                        holder.dataBinding.titleTextView.setText(rewardHistory.reservationName);

                        final int DP_4 = ScreenUtils.dpToPx(mContext, 4);
                        holder.dataBinding.descriptionTextView.setPadding(0, DP_4, 0, 0);
                        holder.dataBinding.descriptionTextView.setText(R.string.message_reward_issue_sticker);

                        if (DailyTextUtils.isTextEmpty(rewardHistory.date) == true)
                        {
                            holder.dataBinding.dateTextView.setText(null);
                        } else
                        {
                            try
                            {
                                final String text = mContext.getString(R.string.label_reward_payment_deposit, DailyCalendar.convertDateFormatString(rewardHistory.date, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT));
                                int startIndex = text.indexOf("ㅣ");

                                SpannableString spannableString = new SpannableString(text);
                                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.cb3b3b3)), //
                                    startIndex, startIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                holder.dataBinding.dateTextView.setText(spannableString);
                            } catch (ParseException e)
                            {
                                ExLog.d(e.toString());
                            }
                        }

                        final String linkText = mContext.getString(R.string.label_reward_view_reservation);

                        SpannableString spannableString = new SpannableString(linkText);
                        spannableString.setSpan(new UnderlineSpan(), 0, linkText.length(), 0);
                        holder.dataBinding.reservationLinkTextView.setVisibility(View.VISIBLE);
                        holder.dataBinding.reservationLinkTextView.setText(spannableString);
                        break;
                    }

                    case "E":
                    {
                        holder.dataBinding.descriptionLayout.setPadding(0, 0, 0, 0);

                        holder.dataBinding.rewardImageView.setVectorImageResource(R.drawable.vector_ic_icon_reward_history_event);
                        holder.dataBinding.titleTextView.setVisibility(View.GONE);

                        holder.dataBinding.descriptionTextView.setPadding(0, 0, 0, 0);
                        holder.dataBinding.descriptionTextView.setText(R.string.message_reward_issue_sticker);

                        if (DailyTextUtils.isTextEmpty(rewardHistory.date) == true)
                        {
                            holder.dataBinding.dateTextView.setText(null);
                        } else
                        {
                            try
                            {
                                holder.dataBinding.dateTextView.setText(mContext.getString(R.string.label_reward_sticker_issue_date, DailyCalendar.convertDateFormatString(rewardHistory.date, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT)));
                            } catch (ParseException e)
                            {
                                ExLog.d(e.toString());
                            }
                        }

                        holder.dataBinding.reservationLinkTextView.setVisibility(View.GONE);
                        break;
                    }
                }
                break;

            // 스티커 만료
            case EXPIRED_STICKER:
            {
                holder.dataBinding.descriptionLayout.setPadding(0, 0, 0, 0);

                holder.dataBinding.rewardImageView.setVectorImageResource(R.drawable.vector_ic_reward_history_expired);
                holder.dataBinding.titleTextView.setVisibility(View.GONE);

                holder.dataBinding.descriptionTextView.setPadding(0, 0, 0, 0);
                holder.dataBinding.descriptionTextView.setText(mContext.getString(R.string.message_reward_expire_sticker, rewardHistory.expiredStickerCount));

                if (DailyTextUtils.isTextEmpty(rewardHistory.date) == true)
                {
                    holder.dataBinding.dateTextView.setText(null);
                } else
                {
                    try
                    {
                        holder.dataBinding.dateTextView.setText(mContext.getString(R.string.label_reward_sticker_expiration_date, DailyCalendar.convertDateFormatString(rewardHistory.date, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT)));
                    } catch (ParseException e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                holder.dataBinding.reservationLinkTextView.setVisibility(View.GONE);
                break;
            }

            // 쿠폰 발행
            case PUBLISHED_COUPON:
            {
                //
                final int DP_16 = ScreenUtils.dpToPx(mContext, 16);
                final int DP_18 = ScreenUtils.dpToPx(mContext, 18);
                holder.dataBinding.descriptionLayout.setPadding(0, DP_16, 0, DP_18);

                holder.dataBinding.rewardImageView.setVectorImageResource(R.drawable.vector_ic_reward_history_coupon);

                holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
                holder.dataBinding.titleTextView.setText(DailyTextUtils.getPriceFormat(mContext, rewardHistory.couponPrice, false));

                final int DP_4 = ScreenUtils.dpToPx(mContext, 4);
                holder.dataBinding.descriptionTextView.setPadding(0, DP_4, 0, 0);
                holder.dataBinding.descriptionTextView.setText(R.string.message_reward_issue_reward_coupon);

                if (DailyTextUtils.isTextEmpty(rewardHistory.date) == true)
                {
                    holder.dataBinding.dateTextView.setText(null);
                } else
                {
                    try
                    {
                        holder.dataBinding.dateTextView.setText(mContext.getString(R.string.label_reward_coupon_issue_date, DailyCalendar.convertDateFormatString(rewardHistory.date, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT)));
                    } catch (ParseException e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                holder.dataBinding.reservationLinkTextView.setVisibility(View.GONE);
                break;
            }
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder
    {
        LayoutRewardHistoryDataBinding dataBinding;

        public HistoryViewHolder(LayoutRewardHistoryDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;

            dataBinding.reservationLinkTextView.setOnClickListener(v -> {
                if (mOnEventListener != null)
                {
                    mOnEventListener.onClick(dataBinding.getRoot());
                }
            });
        }
    }

    private class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
