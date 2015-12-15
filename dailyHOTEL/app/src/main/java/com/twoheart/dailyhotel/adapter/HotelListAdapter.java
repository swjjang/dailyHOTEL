package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelListFragment;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView.PinnedSectionListAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HotelListAdapter extends ArrayAdapter<HotelListViewItem> implements PinnedSectionListAdapter
{
    private Context context;
    private int resourceId;
    private LayoutInflater inflater;
    private ArrayList<HotelListViewItem> mHoteList;
    private PaintDrawable mPaintDrawable;
    private HotelListFragment.SortType mSortType;

    public HotelListAdapter(Context context, int resourceId, ArrayList<HotelListViewItem> hotelList)
    {
        super(context, resourceId, hotelList);

        if (mHoteList == null)
        {
            mHoteList = new ArrayList<HotelListViewItem>();
        }

        mHoteList.clear();
        mHoteList.addAll(hotelList);

        this.context = context;
        this.resourceId = resourceId;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    @Override
    public void clear()
    {
        if (mHoteList == null)
        {
            mHoteList = new ArrayList<HotelListViewItem>();
        }

        mHoteList.clear();

        super.clear();
    }

    @Override
    public HotelListViewItem getItem(int position)
    {
        if (mHoteList == null)
        {
            return null;
        }

        return mHoteList.get(position);
    }

    @Override
    public int getCount()
    {
        if (mHoteList == null)
        {
            return 0;
        }

        return mHoteList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        HotelListViewItem item = getItem(position);

        switch (item.getType())
        {
            case HotelListViewItem.TYPE_SECTION:
            {
                HeaderListViewHolder headerViewHolder = null;

                if (convertView != null)
                {
                    Object tag = convertView.getTag();

                    if (tag != null && tag instanceof HeaderListViewHolder)
                    {
                        headerViewHolder = (HeaderListViewHolder) convertView.getTag();
                    }
                }

                if (headerViewHolder == null)
                {
                    convertView = inflater.inflate(R.layout.list_row_hotel_section, parent, false);
                    headerViewHolder = new HeaderListViewHolder();
                    headerViewHolder.regionDetailName = (TextView) convertView.findViewById(R.id.hotelListRegionName);

                    convertView.setTag(headerViewHolder);
                }

                headerViewHolder.regionDetailName.setText(item.getCategory());
                break;
            }

            case HotelListViewItem.TYPE_ENTRY:
            {
                convertView = makeEntryView(convertView, parent, item);
                break;
            }
        }

        return convertView;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == HotelListViewItem.TYPE_SECTION;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).getType();
    }

    private View makeEntryView(View convertView, ViewGroup parent, HotelListViewItem hotelListViewItem)
    {
        final Hotel element = hotelListViewItem.getItem();
        HotelListViewHolder viewHolder = null;

        if (convertView != null)
        {
            Object tag = convertView.getTag();

            if (tag != null && tag instanceof HotelListViewHolder)
            {
                viewHolder = (HotelListViewHolder) convertView.getTag();
            }
        }

        if (viewHolder == null)
        {
            convertView = inflater.inflate(resourceId, parent, false);

            viewHolder = new HotelListViewHolder();
            viewHolder.hotelLayout = (RelativeLayout) convertView.findViewById(R.id.ll_hotel_row_content);
            viewHolder.hotelImageView = (ImageView) convertView.findViewById(R.id.iv_hotel_row_img);
            viewHolder.hotelNameView = (TextView) convertView.findViewById(R.id.tv_hotel_row_name);
            viewHolder.hotelPriceView = (TextView) convertView.findViewById(R.id.tv_hotel_row_price);
            viewHolder.satisfactionView = (TextView) convertView.findViewById(R.id.satisfactionView);
            viewHolder.hotelDiscountView = (TextView) convertView.findViewById(R.id.tv_hotel_row_discount);
            viewHolder.hotelSoldOutView = (TextView) convertView.findViewById(R.id.tv_hotel_row_soldout);
            viewHolder.hotelAddressView = (TextView) convertView.findViewById(R.id.tv_hotel_row_address);
            viewHolder.hotelGradeView = (TextView) convertView.findViewById(R.id.hv_hotel_grade);
            viewHolder.dBenefitView = convertView.findViewById(R.id.dBenefitImageView);
            viewHolder.averageView = convertView.findViewById(R.id.averageTextView);
            viewHolder.distanceView = (TextView) convertView.findViewById(R.id.distanceTextView);

            convertView.setTag(viewHolder);
        }

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

        viewHolder.hotelAddressView.setText(address);
        viewHolder.hotelNameView.setText(element.getName());

        Spanned currency = Html.fromHtml(context.getResources().getString(R.string.currency));

        if (price <= 0)
        {
            viewHolder.hotelPriceView.setVisibility(View.INVISIBLE);
            viewHolder.hotelPriceView.setText(null);
        } else
        {
            viewHolder.hotelPriceView.setVisibility(View.VISIBLE);
            viewHolder.hotelPriceView.setText(strPrice + currency);
            viewHolder.hotelPriceView.setPaintFlags(viewHolder.hotelPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 만족도
        if (element.satisfaction > 0)
        {
            viewHolder.satisfactionView.setVisibility(View.VISIBLE);
            viewHolder.satisfactionView.setText(element.satisfaction + "%");
        } else
        {
            viewHolder.satisfactionView.setVisibility(View.GONE);
        }

        if (element.nights > 1)
        {
            viewHolder.averageView.setVisibility(View.VISIBLE);
        } else
        {
            viewHolder.averageView.setVisibility(View.GONE);
        }

        viewHolder.hotelDiscountView.setText(strDiscount + currency);
        viewHolder.hotelNameView.setSelected(true); // Android TextView marquee bug

        if (Util.isOverAPI16() == true)
        {
            viewHolder.hotelLayout.setBackground(mPaintDrawable);
        } else
        {
            viewHolder.hotelLayout.setBackgroundDrawable(mPaintDrawable);
        }

        // grade
        viewHolder.hotelGradeView.setText(element.getCategory().getName(context));
        viewHolder.hotelGradeView.setBackgroundResource(element.getCategory().getColorResId());

        final ImageView placeImageView = viewHolder.hotelImageView;

        if (Util.getLCDWidth(context) < 720)
        {
            Glide.with(context).load(element.imageUrl).crossFade().into(placeImageView);
            Glide.with(context).load(element.imageUrl).downloadOnly(new SimpleTarget<File>(360, 240)
            {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                {
                    FileLruCache.getInstance().put(element.imageUrl, resource.getAbsolutePath());
                }
            });
        } else
        {
            Glide.with(context).load(element.imageUrl).crossFade().into(placeImageView);
            Glide.with(context).load(element.imageUrl).downloadOnly(new SimpleTarget<File>()
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
            viewHolder.hotelSoldOutView.setVisibility(View.VISIBLE);
        } else
        {
            viewHolder.hotelSoldOutView.setVisibility(View.GONE);
        }

        if (element.isDBenefit == true)
        {
            viewHolder.dBenefitView.setVisibility(View.VISIBLE);
        } else
        {
            viewHolder.dBenefitView.setVisibility(View.GONE);
        }

        if (mSortType == HotelListFragment.SortType.DISTANCE)
        {
            viewHolder.distanceView.setVisibility(View.VISIBLE);
            viewHolder.distanceView.setText(new DecimalFormat("#.#").format(element.distance / 1000) + "km");
        } else
        {
            viewHolder.distanceView.setVisibility(View.GONE);
        }

        return convertView;
    }


    private static class HotelListViewHolder
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
    }

    private static class HeaderListViewHolder
    {
        TextView regionDetailName;
    }
}
