package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceBannerViewPagerAdapter;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyViewPagerCircleIndicator;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HotelListAdapter extends PlaceListAdapter implements PinnedSectionRecyclerView.PinnedSectionListAdapter
{
    private static final String BOUTIQUE = "boutique";

    private Constants.SortType mSortType;
    private Category mCategory;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnEventBannerClickListener;
    private int mLastEventBannerPosition;

    private Handler mEventBannerHandler;

    public HotelListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnEventBannerClickListener = eventBannerListener;

        mEventBannerHandler = new EventBannerHandler(this);

        setSortType(Constants.SortType.DEFAULT);
    }

    public void addAll(Collection<? extends PlaceViewItem> collection, Constants.SortType sortType, Category category)
    {
        addAll(collection);

        setSortType(sortType);

        mCategory = category;
    }

    public void setSortType(Constants.SortType sortType)
    {
        mSortType = sortType;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == PlaceViewItem.TYPE_SECTION;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        super.onViewRecycled(holder);

        if (holder instanceof EventBannerViewHolder)
        {
            mEventBannerHandler.removeMessages(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_hotel_section, parent, false);

                return new SectionViewHolder(view);
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_hotel, parent, false);

                return new HoltelViewHolder(view);
            }

            case PlaceViewItem.TYPE_EVENT_BANNER:
            {
                View view = mInflater.inflate(R.layout.list_row_eventbanner, parent, false);

                return new EventBannerViewHolder(view);
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
                onBindViewHolder((HoltelViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item);
                break;

            case PlaceViewItem.TYPE_EVENT_BANNER:
                onBindViewHolder((EventBannerViewHolder) holder, item);
                break;
        }
    }

    private void onBindViewHolder(SectionViewHolder holder, PlaceViewItem placeViewItem)
    {
        holder.regionDetailName.setText(placeViewItem.<String>getItem());
    }

    private void onBindViewHolder(final EventBannerViewHolder holder, PlaceViewItem placeViewItem)
    {
        ArrayList<EventBanner> eventBannerList = placeViewItem.getItem();

        PlaceBannerViewPagerAdapter adapter = new PlaceBannerViewPagerAdapter(mContext, eventBannerList, mOnEventBannerClickListener);
        holder.dailyLoopViewPager.setOnPageChangeListener(null);
        holder.dailyLoopViewPager.setAdapter(adapter);
        holder.viewpagerCircleIndicator.setTotalCount(eventBannerList.size());
        holder.dailyLoopViewPager.setCurrentItem(mLastEventBannerPosition);
        holder.dailyLoopViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                mLastEventBannerPosition = position;

                holder.viewpagerCircleIndicator.setPosition(position);

                nextEventBannerPosition(holder, position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == DailyLoopViewPager.SCROLL_STATE_DRAGGING)
                {
                    mEventBannerHandler.removeMessages(0);
                }
            }
        });

        nextEventBannerPosition(holder, mLastEventBannerPosition);
    }

    private void nextEventBannerPosition(EventBannerViewHolder eventViewHolder, int position)
    {
        mEventBannerHandler.removeMessages(0);

        Message message = new Message();
        message.what = 0;
        message.arg1 = position;
        message.obj = eventViewHolder;
        mEventBannerHandler.sendMessageDelayed(message, 5000);
    }

    private void onBindViewHolder(HoltelViewHolder holder, PlaceViewItem placeViewItem)
    {
        final Hotel hotel = placeViewItem.getItem();

        DecimalFormat comma = new DecimalFormat("###,##0");

        String strPrice = comma.format(hotel.price);
        String strDiscount = comma.format(hotel.averageDiscountPrice);

        String address = hotel.addressSummary;

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.hotelAddressView.setText(address);
        holder.hotelNameView.setText(hotel.name);

        String currency = mContext.getResources().getString(R.string.currency);

        if (hotel.price <= 0 || hotel.price <= hotel.averageDiscountPrice)
        {
            holder.hotelPriceView.setVisibility(View.INVISIBLE);
            holder.hotelPriceView.setText(null);
        } else
        {
            holder.hotelPriceView.setVisibility(View.VISIBLE);
            holder.hotelPriceView.setText(strPrice + currency);
            holder.hotelPriceView.setPaintFlags(holder.hotelPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (hotel.satisfaction > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(hotel.satisfaction + "%");
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        if (hotel.nights > 1)
        {
            holder.averageView.setVisibility(View.VISIBLE);
        } else
        {
            holder.averageView.setVisibility(View.GONE);
        }

        holder.hotelDiscountView.setText(strDiscount + currency);
        holder.hotelNameView.setSelected(true); // Android TextView marquee bug

        if (Util.isOverAPI16() == true)
        {
            holder.gradientView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientView.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.hotelGradeView.setText(hotel.getGrade().getName(mContext));
        holder.hotelGradeView.setBackgroundResource(hotel.getGrade().getColorResId());

        Util.requestImageResize(mContext, holder.hotelImageView, hotel.imageUrl);

        // SOLD OUT 표시
        if (hotel.isSoldOut == true)
        {
            holder.hotelSoldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.hotelSoldOutView.setVisibility(View.GONE);
        }

        if (Util.isTextEmpty(hotel.dBenefitText) == false)
        {
            holder.dBenefitLayout.setVisibility(View.VISIBLE);
            holder.dBenefitTextView.setText(hotel.dBenefitText);
        } else
        {
            holder.dBenefitLayout.setVisibility(View.GONE);
        }

        if (mSortType == Constants.SortType.DISTANCE)
        {
            holder.distanceTextView.setVisibility(View.VISIBLE);
            holder.distanceTextView.setText("(거리:" + new DecimalFormat("#.#").format(hotel.distance / 1000) + "km)");
        } else
        {
            holder.distanceTextView.setVisibility(View.GONE);
        }
    }

    private class HoltelViewHolder extends RecyclerView.ViewHolder
    {
        View gradientView;
        com.facebook.drawee.view.SimpleDraweeView hotelImageView;
        TextView hotelNameView;
        TextView hotelPriceView;
        TextView hotelDiscountView;
        View hotelSoldOutView;
        TextView hotelAddressView;
        TextView hotelGradeView;
        TextView satisfactionView;
        View averageView;
        TextView dBenefitTextView;
        TextView distanceTextView;
        View dBenefitLayout;

        public HoltelViewHolder(View itemView)
        {
            super(itemView);

            dBenefitLayout = itemView.findViewById(R.id.dBenefitLayout);
            gradientView = itemView.findViewById(R.id.gradientView);
            hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.imageView);
            hotelNameView = (TextView) itemView.findViewById(R.id.nameTextView);
            hotelPriceView = (TextView) itemView.findViewById(R.id.priceTextView);
            satisfactionView = (TextView) itemView.findViewById(R.id.satisfactionView);
            hotelDiscountView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            hotelSoldOutView = itemView.findViewById(R.id.soldoutView);
            hotelAddressView = (TextView) itemView.findViewById(R.id.addressTextView);
            hotelGradeView = (TextView) itemView.findViewById(R.id.gradeTextView);
            dBenefitTextView = (TextView) itemView.findViewById(R.id.dBenefitTextView);
            averageView = itemView.findViewById(R.id.averageTextView);
            distanceTextView = (TextView) itemView.findViewById(R.id.distanceTextView);

            itemView.setOnClickListener(mOnClickListener);
        }
    }

    private class SectionViewHolder extends RecyclerView.ViewHolder
    {
        TextView regionDetailName;

        public SectionViewHolder(View itemView)
        {
            super(itemView);

            regionDetailName = (TextView) itemView.findViewById(R.id.hotelListRegionName);
        }
    }

    private class EventBannerViewHolder extends RecyclerView.ViewHolder
    {
        DailyLoopViewPager dailyLoopViewPager;
        DailyViewPagerCircleIndicator viewpagerCircleIndicator;

        public EventBannerViewHolder(View itemView)
        {
            super(itemView);

            dailyLoopViewPager = (DailyLoopViewPager) itemView.findViewById(R.id.loopViewPager);
            viewpagerCircleIndicator = (DailyViewPagerCircleIndicator) itemView.findViewById(R.id.viewpagerCircleIndicator);

            dailyLoopViewPager.setSlideTime(4);
        }
    }

    private static class EventBannerHandler extends Handler
    {
        private final WeakReference<HotelListAdapter> mWeakReference;

        public EventBannerHandler(HotelListAdapter adapter)
        {
            mWeakReference = new WeakReference<>(adapter);
        }

        @Override
        public void handleMessage(Message msg)
        {
            HotelListAdapter hotelListAdapter = mWeakReference.get();

            if (hotelListAdapter == null)
            {
                return;
            }

            EventBannerViewHolder eventBannerViewHolder = (EventBannerViewHolder) msg.obj;

            if (eventBannerViewHolder != null)
            {
                eventBannerViewHolder.dailyLoopViewPager.setCurrentItem(msg.arg1 + 1);
            }
        }
    }
}
