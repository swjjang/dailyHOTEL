package com.daily.dailyhotel.screen.home.search.stay.inbound.result.campaign;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutListLoadingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchStayCampaignTagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    List<ObjectItem> mList;

    private boolean mDistanceEnabled;
    private boolean mNightsEnabled; // 연박 여부
    private boolean mRewardEnabled;
    private boolean mTrueVREnabled;

    View.OnClickListener mOnClickListener;
    View.OnClickListener mOnWishClickListener;
    View.OnLongClickListener mOnLongClickListener;

    public SearchStayCampaignTagListAdapter(Context context, ArrayList<ObjectItem> arrayList)
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

    public void setTrueVREnabled(boolean enabled)
    {
        mTrueVREnabled = enabled;
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

    public void add(int position, ObjectItem objectItem)
    {
        if (position >= 0 && position < mList.size())
        {
            mList.add(position, objectItem);
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
            case ObjectItem.TYPE_SECTION:
            {
                LayoutSectionDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_section_data, parent, false);

                return new SectionViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_ENTRY:
            {
                DailyStayCardView cardView = new DailyStayCardView(mContext);
                cardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(cardView);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_footer_data, parent, false);

                // 원래 높이 175dp + 상단 툴바 높이 52dp
                viewDataBinding.footerTextView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 227);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }

            case ObjectItem.TYPE_LOADING_VIEW:
            {
                LayoutListLoadingDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_list_loading_data, parent, false);

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
    protected void onBindViewHolder(StayViewHolder holder, ObjectItem objectItem, int position)
    {
        final Stay stay = objectItem.getItem();

        holder.cardView.setStickerVisible(mRewardEnabled && stay.provideRewardSticker);
        holder.cardView.setDeleteVisible(false);
        holder.cardView.setWishVisible(true);
        holder.cardView.setWish(stay.myWish);
        holder.cardView.setImage(stay.imageUrl);
        holder.cardView.setGradeText(stay.grade.getName(mContext));
        holder.cardView.setVRVisible(stay.trueVR && mTrueVREnabled);
        holder.cardView.setReviewText(stay.satisfaction, stay.reviewCount);
        holder.cardView.setNewVisible(stay.newStay);
        holder.cardView.setStayNameText(stay.name);

        if (mDistanceEnabled == true)
        {
            holder.cardView.setDistanceVisible(true);
            holder.cardView.setDistanceText(stay.distance);
        } else
        {
            holder.cardView.setDistanceVisible(false);
        }

        holder.cardView.setAddressText(stay.addressSummary);

        if (stay.soldOut == true)
        {
            holder.cardView.setPriceText(0, 0, 0, null, false);
        } else
        {
            holder.cardView.setPriceText(stay.discountRate, stay.discountPrice, stay.price, stay.couponDiscountText, mNightsEnabled);
        }

        holder.cardView.setBenefitText(stay.dBenefitText);
        holder.cardView.setDividerVisible(position > 0);
    }

    protected class StayViewHolder extends RecyclerView.ViewHolder
    {
        public DailyStayCardView cardView;

        public StayViewHolder(DailyStayCardView cardView)
        {
            super(cardView);

            this.cardView = cardView;

            cardView.setOnClickListener(mOnClickListener);

            if (Util.supportPreview(mContext) == true)
            {
                cardView.setOnLongClickListener(new View.OnLongClickListener()
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
            }

            cardView.setOnWishClickListener(v -> {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(cardView);
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

    protected class BaseDataBindingViewHolder extends RecyclerView.ViewHolder
    {
        public BaseDataBindingViewHolder(ViewDataBinding dataBinding)
        {
            super(dataBinding.getRoot());
        }
    }
}
