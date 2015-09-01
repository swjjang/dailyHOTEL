package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.BaseTicketDto;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FnBTicketViewPagerAdapter extends TicketViewPagerAdapter
{
	public FnBTicketViewPagerAdapter(Context context)
	{
		super(context);
	}

	@Override
	protected void makeLayout(View view, final BaseTicketDto ticketDto)
	{
		RelativeLayout llHotelRowContent = (RelativeLayout) view.findViewById(R.id.ll_hotel_row_content);
		final ImageView img = (ImageView) view.findViewById(R.id.iv_hotel_row_img);
		TextView name = (TextView) view.findViewById(R.id.tv_hotel_row_name);
		TextView priceTextView = (TextView) view.findViewById(R.id.tv_hotel_row_price);
		TextView discountTextView = (TextView) view.findViewById(R.id.tv_hotel_row_discount);
		TextView sold_out = (TextView) view.findViewById(R.id.tv_hotel_row_soldout);
		TextView address = (TextView) view.findViewById(R.id.tv_hotel_row_address);
		TextView grade = (TextView) view.findViewById(R.id.hv_hotel_grade);
		View closeView = view.findViewById(R.id.closeImageVIew);

		DecimalFormat comma = new DecimalFormat("###,##0");

		address.setText(ticketDto.address);
		name.setText(ticketDto.name);

		Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));

		int price = ticketDto.price;

		if (price <= 0)
		{
			priceTextView.setVisibility(View.INVISIBLE);

			priceTextView.setText(null);
		} else
		{
			priceTextView.setVisibility(View.VISIBLE);

			priceTextView.setText(comma.format(price) + currency);
			priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}

		discountTextView.setText(comma.format(ticketDto.discountPrice) + currency);

		name.setSelected(true); // Android TextView marquee bug

		final int colors[] = { Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000") };
		final float positions[] = { 0.0f, 0.01f, 0.02f, 0.17f, 0.38f };

		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());

		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
		{
			@Override
			public Shader resize(int width, int height)
			{
				return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
			}
		};

		p.setShaderFactory(sf);
		llHotelRowContent.setBackgroundDrawable(p);

		// grade
		grade.setText(ticketDto.grade.getName(mContext));
		grade.setBackgroundResource(ticketDto.grade.getColorResId());

		// Used AQuery
		AQuery aquery = new AQuery(view);
		Bitmap cachedImg = VolleyImageLoader.getCache(ticketDto.imageUrl);

		if (cachedImg == null)
		{ // 힛인 밸류가 없다면 이미지를 불러온 후 캐시에 세이브
			BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback()
			{
				@Override
				protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
				{
					VolleyImageLoader.putCache(url, bm);
					super.callback(url, iv, bm, status);
				}
			};

			if (Util.getLCDWidth(mContext) < 720)
			{
				bitmapAjaxCallback.url(ticketDto.imageUrl).animation(AQuery.FADE_IN);
				aquery.id(img).image(ticketDto.imageUrl, false, false, 240, 0, bitmapAjaxCallback);
			} else
			{
				bitmapAjaxCallback.url(ticketDto.imageUrl).animation(AQuery.FADE_IN);
				aquery.id(img).image(bitmapAjaxCallback);
			}
		} else
		{
			aquery.id(img).image(cachedImg);
		}

		// SOLD OUT 표시
		if (ticketDto.isSoldOut == true)
		{
			sold_out.setVisibility(View.VISIBLE);
		} else
		{
			sold_out.setVisibility(View.GONE);
		}

		closeView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mOnUserActionListener != null)
				{
					mOnUserActionListener.onCloseInfoWindowClickListener();
				}
			}
		});

		llHotelRowContent.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mOnUserActionListener != null)
				{
					mOnUserActionListener.onInfoWindowClickListener(ticketDto);
				}
			}
		});
	}
}
