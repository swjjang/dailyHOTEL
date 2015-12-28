package com.twoheart.dailyhotel.screen.gourmetlist;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.PlaceAdapter;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.screen.hotellist.EventBannerViewPagerAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoopViewPager;
import com.twoheart.dailyhotel.view.widget.DailyViewPagerCircleIndicator;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecycleView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class GourmetAdapter extends PlaceAdapter implements PinnedSectionRecycleView.PinnedSectionListAdapter
{
    private Constants.SortType mSortType;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnEventBannerClickListener;
    private int mLastEventBannerPosition;

    private Handler mEventBannerHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            EventBannerViewHolder eventBannerViewHolder = (EventBannerViewHolder) msg.obj;

            if (eventBannerViewHolder != null)
            {
                eventBannerViewHolder.loopViewPager.setCurrentItem(msg.arg1 + 1);
            }
        }
    };

    public GourmetAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener, View.OnClickListener eventBannerListener)
    {
        super(context, arrayList);

        mOnClickListener = listener;
        mOnEventBannerClickListener = eventBannerListener;

        setSortType(Constants.SortType.DEFAULT);
    }

    public void addAll(Collection<? extends PlaceViewItem> collection, Constants.SortType sortType)
    {
        addAll(collection);

        setSortType(sortType);
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
                View view = mInflater.inflate(R.layout.list_row_gourmet, parent, false);

                return new GourmetViewHolder(view);
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

        switch (item.getType())
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((GourmetViewHolder) holder, item);
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
        ArrayList<EventBanner> eventBannerList = placeViewItem.<ArrayList<EventBanner>>getItem();

        EventBannerViewPagerAdapter adapter = new EventBannerViewPagerAdapter(mContext, eventBannerList, mOnEventBannerClickListener);
        holder.loopViewPager.setOnPageChangeListener(null);
        holder.loopViewPager.setAdapter(adapter);
        holder.viewpagerCircleIndicator.setTotalCount(eventBannerList.size());
        holder.loopViewPager.setCurrentItem(mLastEventBannerPosition);
        holder.loopViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
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
                if (state == LoopViewPager.SCROLL_STATE_DRAGGING)
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

    private void onBindViewHolder(GourmetViewHolder holder, PlaceViewItem placeViewItem)
    {
        final Gourmet gourmet = placeViewItem.<Gourmet>getItem();

        DecimalFormat comma = new DecimalFormat("###,##0");
        int price = gourmet.price;

        String strPrice = comma.format(price);
        String strDiscount = comma.format(gourmet.discountPrice);

        String address = gourmet.address;

        if (address.indexOf('|') >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.hotelAddressView.setText(address);
        holder.hotelNameView.setText(gourmet.name);

        // 인원
        if (gourmet.persons > 1)
        {
            holder.personsTextView.setVisibility(View.VISIBLE);
            holder.personsTextView.setText(mContext.getString(R.string.label_persions, gourmet.persons));
        } else
        {
            holder.personsTextView.setVisibility(View.GONE);
        }

        Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));

        if (price <= 0)
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
        if (gourmet.satisfaction > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(gourmet.satisfaction + "%");
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        holder.hotelDiscountView.setText(strDiscount + currency);
        holder.hotelNameView.setSelected(true); // Android TextView marquee bug

        if (Util.isOverAPI16() == true)
        {
            holder.hotelLayout.setBackground(mPaintDrawable);
        } else
        {
            holder.hotelLayout.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        if (Util.isTextEmpty(gourmet.category) == true)
        {
            holder.hotelGradeView.setVisibility(View.GONE);
        } else
        {
            holder.hotelGradeView.setVisibility(View.VISIBLE);
            holder.hotelGradeView.setText(gourmet.category);
        }

        final ImageView placeImageView = holder.hotelImageView;

        if (Util.getLCDWidth(mContext) < 720)
        {
            Glide.with(mContext).load(gourmet.imageUrl).crossFade().into(placeImageView);
            Glide.with(mContext).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>(360, 240)
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(gourmet.imageUrl, resource.getAbsolutePath());
                }
            });
        } else
        {
            Glide.with(mContext).load(gourmet.imageUrl).crossFade().into(placeImageView);
            Glide.with(mContext).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>()
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(gourmet.imageUrl, resource.getAbsolutePath());
                }
            });
        }

        // SOLD OUT 표시
        if (gourmet.isSoldOut)
        {
            holder.hotelSoldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.hotelSoldOutView.setVisibility(View.GONE);
        }

        if (mSortType == Constants.SortType.DISTANCE)
        {
            holder.distanceView.setVisibility(View.VISIBLE);
            holder.distanceView.setText(new DecimalFormat("#.#").format(gourmet.distance / 1000) + "km");
        } else
        {
            holder.distanceView.setVisibility(View.GONE);
        }
    }

    private class GourmetViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout hotelLayout;
        ImageView hotelImageView;
        TextView hotelNameView;
        TextView hotelPriceView;
        TextView hotelDiscountView;
        TextView hotelSoldOutView;
        TextView hotelAddressView;
        TextView hotelGradeView;
        TextView satisfactionView;
        TextView personsTextView;
        TextView distanceView;

        public GourmetViewHolder(View itemView)
        {
            super(itemView);

            hotelLayout = (RelativeLayout) itemView.findViewById(R.id.ll_hotel_row_content);
            hotelImageView = (ImageView) itemView.findViewById(R.id.iv_hotel_row_img);
            hotelNameView = (TextView) itemView.findViewById(R.id.tv_hotel_row_name);
            hotelPriceView = (TextView) itemView.findViewById(R.id.tv_hotel_row_price);
            satisfactionView = (TextView) itemView.findViewById(R.id.satisfactionView);
            hotelDiscountView = (TextView) itemView.findViewById(R.id.tv_hotel_row_discount);
            hotelSoldOutView = (TextView) itemView.findViewById(R.id.tv_hotel_row_soldout);
            hotelAddressView = (TextView) itemView.findViewById(R.id.tv_hotel_row_address);
            hotelGradeView = (TextView) itemView.findViewById(R.id.hv_hotel_grade);
            personsTextView = (TextView) itemView.findViewById(R.id.personsTextView);
            distanceView = (TextView) itemView.findViewById(R.id.distanceTextView);

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
        LoopViewPager loopViewPager;
        DailyViewPagerCircleIndicator viewpagerCircleIndicator;

        public EventBannerViewHolder(View itemView)
        {
            super(itemView);

            loopViewPager = (LoopViewPager) itemView.findViewById(R.id.loopViewPager);
            viewpagerCircleIndicator = (DailyViewPagerCircleIndicator) itemView.findViewById(R.id.viewpagerCircleIndicator);
        }
    }
}
