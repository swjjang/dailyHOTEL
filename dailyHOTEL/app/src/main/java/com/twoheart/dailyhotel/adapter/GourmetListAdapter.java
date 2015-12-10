package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.PlaceViewItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GourmetListAdapter extends PlaceListAdapter
{
    public GourmetListAdapter(Context context, int resourceId, ArrayList<PlaceViewItem> arrayList)
    {
        super(context, resourceId, arrayList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        PlaceViewItem item = getItem(position);

        switch (item.type)
        {
            case PlaceViewItem.TYPE_SECTION:
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
                    headerViewHolder.titleTextView = (TextView) convertView.findViewById(R.id.hotelListRegionName);

                    convertView.setTag(headerViewHolder);
                }

                headerViewHolder.titleTextView.setText(item.title);
                break;
            }

            case PlaceViewItem.TYPE_ENTRY:
            {
                final Gourmet gourmet = (Gourmet) item.getPlace();
                PlaceViewHolder viewHolder = null;

                if (convertView != null)
                {
                    Object tag = convertView.getTag();

                    if (tag != null && tag instanceof PlaceViewHolder)
                    {
                        viewHolder = (PlaceViewHolder) convertView.getTag();
                    }
                }

                if (viewHolder == null)
                {
                    convertView = inflater.inflate(resourceId, parent, false);

                    viewHolder = new PlaceViewHolder();
                    viewHolder.hotelLayout = (RelativeLayout) convertView.findViewById(R.id.ll_hotel_row_content);
                    viewHolder.hotelImageView = (ImageView) convertView.findViewById(R.id.iv_hotel_row_img);
                    viewHolder.hotelNameView = (TextView) convertView.findViewById(R.id.tv_hotel_row_name);
                    viewHolder.hotelPriceView = (TextView) convertView.findViewById(R.id.tv_hotel_row_price);
                    viewHolder.satisfactionView = (TextView) convertView.findViewById(R.id.satisfactionView);
                    viewHolder.hotelDiscountView = (TextView) convertView.findViewById(R.id.tv_hotel_row_discount);
                    viewHolder.hotelSoldOutView = (TextView) convertView.findViewById(R.id.tv_hotel_row_soldout);
                    viewHolder.hotelAddressView = (TextView) convertView.findViewById(R.id.tv_hotel_row_address);
                    viewHolder.hotelGradeView = (TextView) convertView.findViewById(R.id.hv_hotel_grade);
                    viewHolder.personsTextView = (TextView) convertView.findViewById(R.id.personsTextView);

                    convertView.setTag(viewHolder);
                }

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

                viewHolder.hotelAddressView.setText(address);
                viewHolder.hotelNameView.setText(gourmet.name);

                // 인원
                if (gourmet.persons > 1)
                {
                    viewHolder.personsTextView.setVisibility(View.VISIBLE);
                    viewHolder.personsTextView.setText(context.getString(R.string.label_persions, gourmet.persons));
                } else
                {
                    viewHolder.personsTextView.setVisibility(View.GONE);
                }

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
                if (gourmet.satisfaction > 0)
                {
                    viewHolder.satisfactionView.setVisibility(View.VISIBLE);
                    viewHolder.satisfactionView.setText(gourmet.satisfaction + "%");
                } else
                {
                    viewHolder.satisfactionView.setVisibility(View.GONE);
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
                viewHolder.hotelGradeView.setText(gourmet.category);

                final ImageView placeImageView = viewHolder.hotelImageView;

                if (Util.getLCDWidth(context) < 720)
                {
                    Glide.with(context).load(gourmet.imageUrl).crossFade().into(placeImageView);
                    Glide.with(context).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>(360, 240)
                    {
                        @Override
                        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation)
                        {
                            FileLruCache.getInstance().put(gourmet.imageUrl, resource.getAbsolutePath());
                        }
                    });
                } else
                {
                    Glide.with(context).load(gourmet.imageUrl).crossFade().into(placeImageView);
                    Glide.with(context).load(gourmet.imageUrl).downloadOnly(new SimpleTarget<File>()
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
                    viewHolder.hotelSoldOutView.setVisibility(View.VISIBLE);
                } else
                {
                    viewHolder.hotelSoldOutView.setVisibility(View.GONE);
                }
                break;
            }
        }

        return convertView;
    }

    private static class PlaceViewHolder
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
    }

    private static class HeaderListViewHolder
    {
        TextView titleTextView;
    }
}
