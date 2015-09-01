package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.BaseTicketDto;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FnBTicketListAdapter extends TicketListAdapter
{
	public FnBTicketListAdapter(Context context, int resourceId, ArrayList<TicketViewItem> arrayList)
	{
		super(context, resourceId, arrayList);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		TicketViewItem item = getItem(position);

		switch (item.type)
		{
			case TicketViewItem.TYPE_SECTION:
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

			case TicketViewItem.TYPE_ENTRY:
			{
				BaseTicketDto ticketDto = item.getTicketDto();
				TicketViewHolder viewHolder = null;

				if (convertView != null)
				{
					Object tag = convertView.getTag();

					if (tag != null && tag instanceof TicketViewHolder)
					{
						viewHolder = (TicketViewHolder) convertView.getTag();
					}
				}

				if (viewHolder == null)
				{
					convertView = inflater.inflate(resourceId, parent, false);

					viewHolder = new TicketViewHolder();
					viewHolder.llHotelRowContent = (RelativeLayout) convertView.findViewById(R.id.ll_hotel_row_content);
					viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_hotel_row_img);
					viewHolder.name = (TextView) convertView.findViewById(R.id.tv_hotel_row_name);
					viewHolder.price = (TextView) convertView.findViewById(R.id.tv_hotel_row_price);
					viewHolder.discount = (TextView) convertView.findViewById(R.id.tv_hotel_row_discount);
					viewHolder.sold_out = (TextView) convertView.findViewById(R.id.tv_hotel_row_soldout);
					viewHolder.address = (TextView) convertView.findViewById(R.id.tv_hotel_row_address);
					viewHolder.grade = (TextView) convertView.findViewById(R.id.hv_hotel_grade);

					convertView.setTag(viewHolder);
				}

				DecimalFormat comma = new DecimalFormat("###,##0");
				int price = ticketDto.price;

				String strPrice = comma.format(price);
				String strDiscount = comma.format(ticketDto.discountPrice);

				viewHolder.address.setText(ticketDto.address);
				viewHolder.name.setText(ticketDto.name);

				Spanned currency = Html.fromHtml(context.getResources().getString(R.string.currency));

				if (price <= 0)
				{
					viewHolder.price.setVisibility(View.INVISIBLE);
					viewHolder.price.setText(null);
				} else
				{
					viewHolder.price.setVisibility(View.VISIBLE);

					viewHolder.price.setText(strPrice + currency);
					viewHolder.price.setPaintFlags(viewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				}

				viewHolder.discount.setText(strDiscount + currency);
				viewHolder.name.setSelected(true); // Android TextView marquee bug

				if (Util.isOverAPI16() == true)
				{
					viewHolder.llHotelRowContent.setBackground(mPaintDrawable);
				} else
				{
					viewHolder.llHotelRowContent.setBackgroundDrawable(mPaintDrawable);
				}

				// grade
				viewHolder.grade.setText(ticketDto.grade.getName(context));
				viewHolder.grade.setBackgroundResource(ticketDto.grade.getColorResId());

				// AQuery사용시 
				AQuery aquery = new AQuery(convertView);
				Bitmap cachedImg = VolleyImageLoader.getCache(ticketDto.imageUrl);

				if (cachedImg == null)
				{
					BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback()
					{
						@Override
						protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
						{
							VolleyImageLoader.putCache(url, bm);
							super.callback(url, iv, bm, status);
						}
					};

					if (Util.getLCDWidth(context) < 720)
					{
						bitmapAjaxCallback.url(ticketDto.imageUrl).animation(AQuery.FADE_IN);
						aquery.id(viewHolder.img).image(ticketDto.imageUrl, false, false, 240, 0, bitmapAjaxCallback);
					} else
					{
						bitmapAjaxCallback.url(ticketDto.imageUrl).animation(AQuery.FADE_IN);
						aquery.id(viewHolder.img).image(bitmapAjaxCallback);
					}
				} else
				{
					viewHolder.img.setImageBitmap(cachedImg);
				}

				// SOLD OUT 표시
				if (ticketDto.isSoldOut)
				{
					viewHolder.sold_out.setVisibility(View.VISIBLE);
				} else
				{
					viewHolder.sold_out.setVisibility(View.GONE);
				}
				break;
			}
		}

		return convertView;
	}

	private class TicketViewHolder
	{
		RelativeLayout llHotelRowContent;
		ImageView img;
		TextView name;
		TextView price;
		TextView discount;
		TextView sold_out;
		TextView address;
		TextView grade;
	}

	private class HeaderListViewHolder
	{
		TextView titleTextView;
	}
}
