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
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.screen.gourmetlist.GourmetListFragment;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.GourmetViewItem;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecycleView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class GourmetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecycleView.PinnedSectionListAdapter
{
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected PaintDrawable mPaintDrawable;
    private ArrayList<PlaceViewItem> mGourmetViewItemList;
    private GourmetListFragment.OnItemClickListener mOnItemClickListener;

    protected GourmetListFragment.SortType mSortType = GourmetListFragment.SortType.DEFAULT;

    public GourmetAdapter(Context context, ArrayList<PlaceViewItem> arrayList, GourmetListFragment.OnItemClickListener listener)
    {
        if (mGourmetViewItemList == null)
        {
            mGourmetViewItemList = new ArrayList<>();
        }

        mContext = context;

        mGourmetViewItemList.clear();
        mGourmetViewItemList.addAll(arrayList);

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
        if (mGourmetViewItemList == null)
        {
            mGourmetViewItemList = new ArrayList<>();
        }

        mGourmetViewItemList.clear();
    }

    public PlaceViewItem getItem(int position)
    {
        if (mGourmetViewItemList == null)
        {
            return null;
        }

        return mGourmetViewItemList.get(position);
    }

    public void addAll(Collection<? extends PlaceViewItem> collection, GourmetListFragment.SortType sortType)
    {
        if (collection == null)
        {
            return;
        }

        if (mGourmetViewItemList == null)
        {
            mGourmetViewItemList = new ArrayList<>();
        }

        setSortType(sortType);

        mGourmetViewItemList.addAll(collection);
    }

    public void setSortType(GourmetListFragment.SortType sortType)
    {
        mSortType = sortType;
    }

    public ArrayList<PlaceViewItem> getData()
    {
        return mGourmetViewItemList;
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
            case GourmetViewItem.TYPE_SECTION:
            {
                View view = mInflater.inflate(R.layout.list_row_hotel_section, parent, false);

                return new SectionViewHolder(view);
            }

            case GourmetViewItem.TYPE_ENTRY:
            {
                View view = mInflater.inflate(R.layout.list_row_gourmet, parent, false);

                return new GourmetViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        GourmetViewItem item = (GourmetViewItem) getItem(position);

        if (holder instanceof SectionViewHolder)
        {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            sectionViewHolder.regionDetailName.setText(item.title);

        } else if (holder instanceof GourmetViewHolder)
        {
            makeEntryView((GourmetViewHolder) holder, item);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).type;
    }

    @Override
    public int getItemCount()
    {
        if (mGourmetViewItemList == null)
        {
            return 0;
        }

        return mGourmetViewItemList.size();
    }

    private void makeEntryView(GourmetViewHolder holder, GourmetViewItem gourmetViewItem)
    {
        final Gourmet gourmet = (Gourmet) gourmetViewItem.getPlace();

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

        if (mSortType == GourmetListFragment.SortType.DISTANCE)
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
