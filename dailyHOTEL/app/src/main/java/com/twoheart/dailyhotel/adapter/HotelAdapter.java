package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.screen.hotellist.HotelListFragment;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecycleView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HotelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecycleView.PinnedSectionListAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<HotelListViewItem> mHoteList;
    private PaintDrawable mPaintDrawable;
    private HotelListFragment.SortType mSortType;
    private HotelListFragment.OnItemClickListener mOnItemClickListener;

    public HotelAdapter(Context context, ArrayList<HotelListViewItem> hotelList, HotelListFragment.OnItemClickListener listener)
    {
        if (mHoteList == null)
        {
            mHoteList = new ArrayList<HotelListViewItem>();
        }

        mContext = context;

        mHoteList.clear();
        mHoteList.addAll(hotelList);

        mOnItemClickListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.38f};

        mPaintDrawable = new PaintDrawable();
        mPaintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        mPaintDrawable.setShaderFactory(sf);
    }

    public void clear()
    {
        if (mHoteList == null)
        {
            mHoteList = new ArrayList<HotelListViewItem>();
        }

        mHoteList.clear();
    }

    public HotelListViewItem getItem(int position)
    {
        if (mHoteList == null)
        {
            return null;
        }

        return mHoteList.get(position);
    }

    public void addAll(Collection<? extends HotelListViewItem> collection, HotelListFragment.SortType sortType)
    {
        if (collection == null)
        {
            return;
        }

        if (mHoteList == null)
        {
            mHoteList = new ArrayList<HotelListViewItem>();
        }

        setSortType(sortType);

        mHoteList.addAll(collection);
    }

    public void setSortType(HotelListFragment.SortType sortType)
    {
        mSortType = sortType;
    }

    public ArrayList<HotelListViewItem> getData()
    {
        return mHoteList;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == HotelListViewItem.TYPE_SECTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case HotelListViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_hotel_section, parent, false);

                return new SectionViewHolder(view);
            }

            case HotelListViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_hotel, parent, false);

                return new HoltelViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        HotelListViewItem item = getItem(position);

        if (holder instanceof SectionViewHolder)
        {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.regionDetailName.setText(item.getCategory());

        } else if (holder instanceof HoltelViewHolder)
        {
            makeEntryView((HoltelViewHolder) holder, item);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).getType();
    }

    @Override
    public int getItemCount()
    {
        if (mHoteList == null)
        {
            return 0;
        }

        return mHoteList.size();
    }

    private void makeEntryView(HoltelViewHolder holder, HotelListViewItem hotelListViewItem)
    {
        final Hotel element = hotelListViewItem.getItem();

        DecimalFormat comma = new DecimalFormat("###,##0");
        int price = element.getPrice();

        String strPrice = comma.format(price);
        String strDiscount = comma.format(element.averageDiscount);

        String address = element.getAddress();

        int barIndex = address.indexOf('|');
        if (barIndex >= 0)
        {
            address = address.replace(" | ", "ㅣ");
        } else if (address.indexOf('l') >= 0)
        {
            address = address.replace(" l ", "ㅣ");
        }

        holder.hotelAddressView.setText(address);
        holder.hotelNameView.setText(element.getName());

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
        if (element.satisfaction > 0)
        {
            holder.satisfactionView.setVisibility(View.VISIBLE);
            holder.satisfactionView.setText(element.satisfaction + "%");
        } else
        {
            holder.satisfactionView.setVisibility(View.GONE);
        }

        if (element.nights > 1)
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
            holder.hotelLayout.setBackground(mPaintDrawable);
        } else
        {
            holder.hotelLayout.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        holder.hotelGradeView.setText(element.getCategory().getName(mContext));
        holder.hotelGradeView.setBackgroundResource(element.getCategory().getColorResId());

        final ImageView placeImageView = holder.hotelImageView;

        if (Util.getLCDWidth(mContext) < 720)
        {
            Glide.with(mContext).load(element.imageUrl).crossFade().into(placeImageView);
            Glide.with(mContext).load(element.imageUrl).downloadOnly(new SimpleTarget<File>(360, 240)
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(element.imageUrl, resource.getAbsolutePath());
                }
            });
        } else
        {
            Glide.with(mContext).load(element.imageUrl).crossFade().into(placeImageView);
            Glide.with(mContext).load(element.imageUrl).downloadOnly(new SimpleTarget<File>()
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(element.imageUrl, resource.getAbsolutePath());
                }
            });
        }

        int availableRoomCount = element.getAvailableRoom();

        // SOLD OUT 표시
        if (availableRoomCount == 0)
        {
            holder.hotelSoldOutView.setVisibility(View.VISIBLE);
        } else
        {
            holder.hotelSoldOutView.setVisibility(View.GONE);
        }

        if (element.isDBenefit == true)
        {
            holder.dBenefitView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dBenefitView.setVisibility(View.GONE);
        }

        if (mSortType == HotelListFragment.SortType.DISTANCE)
        {
            holder.distanceView.setVisibility(View.VISIBLE);
            holder.distanceView.setText(new DecimalFormat("#.#").format(element.distance / 1000) + "km");
        } else
        {
            holder.distanceView.setVisibility(View.GONE);
        }
    }

    private class HoltelViewHolder extends RecyclerView.ViewHolder
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
        View averageView;
        View dBenefitView;
        TextView distanceView;

        public HoltelViewHolder(View itemView)
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
            dBenefitView = itemView.findViewById(R.id.dBenefitImageView);
            averageView = itemView.findViewById(R.id.averageTextView);
            distanceView = (TextView) itemView.findViewById(R.id.distanceTextView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mOnItemClickListener != null)
                    {
                        mOnItemClickListener.onItemClick(v);
                    }
                }
            });
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
}
