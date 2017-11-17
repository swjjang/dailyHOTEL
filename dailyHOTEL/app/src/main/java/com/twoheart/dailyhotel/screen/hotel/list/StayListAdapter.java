package com.twoheart.dailyhotel.screen.hotel.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutSectionDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class StayListAdapter extends PlaceListAdapter
{
    protected int mNights;
    View.OnClickListener mOnClickListener;

    public StayListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnEventBannerClickListener = eventBannerListener;

        setSortType(Constants.SortType.DEFAULT);
    }

    @Override
    public void setPlaceBookingDay(PlaceBookingDay placeBookingDay)
    {
        if (placeBookingDay == null)
        {
            return;
        }

        try
        {
            mNights = ((StayBookingDay) placeBookingDay).getNights();
        } catch (Exception e)
        {
            mNights = 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_SECTION:
            {
                LayoutSectionDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_section_data, parent, false);

                return new SectionViewHolder(viewDataBinding);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                DailyStayCardView stayCardView = new DailyStayCardView(mContext);
                stayCardView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                return new StayViewHolder(stayCardView);
            }

            case PlaceViewItem.TYPE_EVENT_BANNER:
            {
                View view = mInflater.inflate(R.layout.list_row_eventbanner, parent, false);

                return new EventBannerViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                LayoutFooterDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_footer_data, parent, false);

                // 원래 높이 175dp + 상단 툴바 높이 52dp
                viewDataBinding.footerTextView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 227);

                return new BaseDataBindingViewHolder(viewDataBinding);
            }

            case PlaceViewItem.TYPE_LOADING_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_loading, parent, false);

                return new BaseViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((StayViewHolder) holder, item, position);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_EVENT_BANNER:
                onBindViewHolder((EventBannerViewHolder) holder, item);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onBindViewHolder(StayViewHolder holder, PlaceViewItem placeViewItem, int position)
    {
        final Stay stay = placeViewItem.getItem();

        holder.stayCardView.setStickerVisible(mRewardEnabled && stay.provideRewardSticker);
        holder.stayCardView.setDeleteVisible(false);
        holder.stayCardView.setWishVisible(true);
        holder.stayCardView.setWish(stay.myWish);

        holder.stayCardView.setImage(stay.imageUrl);

        holder.stayCardView.setGradeText(stay.getGrade().getName(mContext));
        holder.stayCardView.setVRVisible(stay.truevr && mTrueVREnabled);
        holder.stayCardView.setReviewText(stay.satisfaction, stay.reviewCount);

        holder.stayCardView.setNewVisible(stay.newItem);

        holder.stayCardView.setStayNameText(stay.name);

        if (mShowDistanceIgnoreSort == true || getSortType() == Constants.SortType.DISTANCE)
        {
            holder.stayCardView.setDistanceVisible(true);
            holder.stayCardView.setDistanceText(stay.distance);
        } else
        {
            holder.stayCardView.setDistanceVisible(false);
        }

        holder.stayCardView.setAddressText(stay.addressSummary);

        if (stay.isSoldOut == true)
        {
            holder.stayCardView.setPriceText(0, 0, 0, null, false);
        } else
        {
            holder.stayCardView.setPriceText(stay.discountRate, stay.discountPrice, stay.price, stay.couponDiscountText, mNights > 1);
        }

        holder.stayCardView.setBenefitText(stay.dBenefitText);

        if (position == 0 || getItem(position - 1).mType != PlaceViewItem.TYPE_SECTION)
        {
            holder.stayCardView.setDividerVisible(true);
        } else
        {
            holder.stayCardView.setDividerVisible(false);
        }
    }

    protected class StayViewHolder extends RecyclerView.ViewHolder
    {
        public DailyStayCardView stayCardView;

        public StayViewHolder(DailyStayCardView stayCardView)
        {
            super(stayCardView);

            this.stayCardView = stayCardView;

            itemView.setOnClickListener(mOnClickListener);

            if (Util.supportPreview(mContext) == true)
            {
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
            }

            stayCardView.setOnWishClickListener(v ->
            {
                if (mOnWishClickListener != null)
                {
                    mOnWishClickListener.onClick(stayCardView);
                }
            });
        }
    }
}
