package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HotelSearchResultListAdapter extends PlaceListAdapter
{
    private Constants.SortType mSortType;
    private View.OnClickListener mOnClickListener;
    private Context mContext;

    public HotelSearchResultListAdapter(Context context, ArrayList<PlaceViewItem> arrayList, View.OnClickListener listener)
    {
        super(context, arrayList);

        mContext = context;
        mOnClickListener = listener;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
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
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.getType())
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((HoltelViewHolder) holder, item);
                break;
        }
    }

    private void onBindViewHolder(HoltelViewHolder holder, PlaceViewItem placeViewItem)
    {
        final Hotel hotel = placeViewItem.<Hotel>getItem();

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

        if (hotel.isDBenefit == true)
        {
            holder.dBenefitView.setVisibility(View.VISIBLE);
        } else
        {
            holder.dBenefitView.setVisibility(View.GONE);
        }

        if (mSortType == Constants.SortType.DISTANCE)
        {
            holder.distanceView.setVisibility(View.VISIBLE);
            holder.distanceView.setText(new DecimalFormat("#.#").format(hotel.distance / 1000) + "km");
        } else
        {
            holder.distanceView.setVisibility(View.GONE);
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
        View dBenefitView;
        TextView distanceView;

        public HoltelViewHolder(View itemView)
        {
            super(itemView);

            gradientView = itemView.findViewById(R.id.gradientView);
            hotelImageView = (com.facebook.drawee.view.SimpleDraweeView) itemView.findViewById(R.id.imageView);
            hotelNameView = (TextView) itemView.findViewById(R.id.nameTextView);
            hotelPriceView = (TextView) itemView.findViewById(R.id.priceTextView);
            satisfactionView = (TextView) itemView.findViewById(R.id.satisfactionView);
            hotelDiscountView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            hotelSoldOutView = itemView.findViewById(R.id.soldoutView);
            hotelAddressView = (TextView) itemView.findViewById(R.id.addressTextView);
            hotelGradeView = (TextView) itemView.findViewById(R.id.gradeTextView);
            dBenefitView = itemView.findViewById(R.id.dBenefitImageView);
            averageView = itemView.findViewById(R.id.averageTextView);
            distanceView = (TextView) itemView.findViewById(R.id.distanceTextView);

            itemView.setOnClickListener(mOnClickListener);
        }
    }
}
