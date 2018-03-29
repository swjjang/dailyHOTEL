package com.daily.dailyhotel.screen.home.stay.outbound.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutListLoadingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StayOutboundListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecyclerView.PinnedSectionListAdapter
{
    Context mContext;
    private List<ObjectItem> mList;

    View.OnClickListener mOnClickListener;
    View.OnLongClickListener mOnLongClickListener;
    View.OnClickListener mOnWishClickListener;

    private boolean mDistanceEnabled;
    private boolean mNightsEnabled; // 연박 여부
    private boolean mRewardEnabled;

    public StayOutboundListAdapter(Context context, ArrayList<ObjectItem> arrayList)
    {
        mContext = context;

        mList = new ArrayList<>();

        addAll(arrayList);
    }

    public void setDistanceEnabled(boolean enabled)
    {
        mDistanceEnabled = enabled;
    }

    public void setNightsEnabled(boolean enabled)
    {
        mNightsEnabled = enabled;
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public void setOnClickListener(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener)
    {
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnWishClickListener(View.OnClickListener listener)
    {
        mOnWishClickListener = listener;
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
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == ObjectItem.TYPE_SECTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case ObjectItem.TYPE_SECTION:
            {
                LayoutSectionDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_section_data, parent, false);

                return new SectionViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_ENTRY:
            {
                DailyStayOutboundCardView stayOutboundCardView = new DailyStayOutboundCardView(mContext);
                stayOutboundCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(stayOutboundCardView);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_footer_data, parent, false);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_LOADING_VIEW:
            {
                LayoutListLoadingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_list_loading_data, parent, false);
                dataBinding.progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.probressbar_default), PorterDuff.Mode.SRC_IN);

                return new BaseDataBindingViewHolder(dataBinding);
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
                onBindViewHolder((StayViewHolder) holder, item, position);
                break;

            case ObjectItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;
        }
    }

    protected void onBindViewHolder(SectionViewHolder holder, ObjectItem objectItem)
    {
        if (holder == null || objectItem == null)
        {
            return;
        }

        holder.dataBinding.sectionTextView.setText(objectItem.<String>getItem());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolder(StayViewHolder holder, ObjectItem objectItem, int position)
    {
        if (holder == null || objectItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = objectItem.getItem();

        holder.stayOutboundCardView.setStickerVisible(mRewardEnabled && stayOutbound.provideRewardSticker);
        holder.stayOutboundCardView.setDeleteVisible(false);
        holder.stayOutboundCardView.setWishVisible(true);
        holder.stayOutboundCardView.setWish(stayOutbound.myWish);

        holder.stayOutboundCardView.setImage(stayOutbound.getImageMap());

        if ((int) stayOutbound.rating == 0)
        {
            holder.stayOutboundCardView.setGradeText(null);
        } else
        {
            holder.stayOutboundCardView.setGradeText(mContext.getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating));
        }

        holder.stayOutboundCardView.setVRVisible(false);
        holder.stayOutboundCardView.setRatingText(stayOutbound.tripAdvisorRating);

        holder.stayOutboundCardView.setNewVisible(false);

        holder.stayOutboundCardView.setStayNameText(stayOutbound.name, stayOutbound.nameEng);

        if (mDistanceEnabled == true)
        {
            holder.stayOutboundCardView.setDistanceVisible(true);
            holder.stayOutboundCardView.setDistanceText(stayOutbound.distance);
        } else
        {
            holder.stayOutboundCardView.setDistanceVisible(false);
        }

        holder.stayOutboundCardView.setAddressText(stayOutbound.locationDescription);

        // 1박인 경우 nightlyRate을 사용하지 않고 total을 사용한다.
        if (mNightsEnabled == true)
        {
            holder.stayOutboundCardView.setPriceText(stayOutbound.discountRate, stayOutbound.nightlyRate, stayOutbound.nightlyBaseRate, null, mNightsEnabled);
        } else
        {
            holder.stayOutboundCardView.setPriceText(stayOutbound.discountRate, stayOutbound.total, stayOutbound.nightlyBaseRate, stayOutbound.couponDiscountPriceText, mNightsEnabled);
        }

        holder.stayOutboundCardView.setBenefitText(null);

        if (position == 0 || getItem(position - 1).mType != ObjectItem.TYPE_SECTION)
        {
            holder.stayOutboundCardView.setDividerVisible(true);
        } else
        {
            holder.stayOutboundCardView.setDividerVisible(false);
        }
    }

    protected class StayViewHolder extends RecyclerView.ViewHolder
    {
        DailyStayOutboundCardView stayOutboundCardView;

        public StayViewHolder(DailyStayOutboundCardView stayOutboundCardView)
        {
            super(stayOutboundCardView);

            this.stayOutboundCardView = stayOutboundCardView;

            itemView.setOnClickListener(mOnClickListener);
            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (mOnLongClickListener == null)
                    {
                        return false;
                    } else
                    {
                        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(70);

                        return mOnLongClickListener.onLongClick(v);
                    }
                }
            });

            stayOutboundCardView.setOnWishClickListener(v -> {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(stayOutboundCardView);
                }
            });
        }
    }

    protected class SectionViewHolder extends RecyclerView.ViewHolder
    {
        public LayoutSectionDataBinding dataBinding;

        public SectionViewHolder(LayoutSectionDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
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
