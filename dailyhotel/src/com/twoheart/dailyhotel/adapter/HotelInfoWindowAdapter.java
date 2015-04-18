package com.twoheart.dailyhotel.adapter;

import java.text.DecimalFormat;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.util.VolleyImageLoader;

public class HotelInfoWindowAdapter implements InfoWindowAdapter
{
	private Hotel mHotel;
	private int mHotelIndex;
	private Context mContext;
	private boolean mRefreshingInfoWindow;
	private View mView;

	public HotelInfoWindowAdapter(Context context)
	{
		mContext = context;

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (mView == null)
		{
			mView = layoutInflater.inflate(R.layout.view_map_popup, null);
		}
	}

	public void setHotel(Hotel hotel, int index)
	{
		mHotel = hotel;

		mRefreshingInfoWindow = false;

		if (mView != null)
		{
			ImageView imageView = (ImageView) mView.findViewById(R.id.iv_hotel_row_img);
			imageView.setBackgroundResource(R.drawable.img_placeholder);
		}
	}

	public void setHotelIndex(int index)
	{
		mHotelIndex = index;
	}

	public Hotel getHotel()
	{
		return mHotel;
	}

	public int getHotelIndex()
	{
		return mHotelIndex;
	}

	@Override
	public View getInfoWindow(final Marker marker)
	{
		if (mRefreshingInfoWindow == true)
		{

		} else
		{
			RelativeLayout llHotelRowContent = (RelativeLayout) mView.findViewById(R.id.ll_hotel_row_content);
			final ImageView img = (ImageView) mView.findViewById(R.id.iv_hotel_row_img);
			TextView name = (TextView) mView.findViewById(R.id.tv_hotel_row_name);
			TextView price = (TextView) mView.findViewById(R.id.tv_hotel_row_price);
			TextView discount = (TextView) mView.findViewById(R.id.tv_hotel_row_discount);
			TextView sold_out = (TextView) mView.findViewById(R.id.tv_hotel_row_soldout);
			TextView address = (TextView) mView.findViewById(R.id.tv_hotel_row_address);
			TextView grade = (TextView) mView.findViewById(R.id.hv_hotel_grade);

			DecimalFormat comma = new DecimalFormat("###,##0");
			String strPrice = comma.format(Integer.parseInt(mHotel.getPrice()));
			String strDiscount = comma.format(Integer.parseInt(mHotel.getDiscount()));

			address.setText(mHotel.getAddress());
			name.setText(mHotel.getName());

			Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));
			price.setText(strPrice + currency);
			price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			discount.setText(strDiscount + currency);

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
			grade.setText(mHotel.getCategory().getName(mContext));
			grade.setBackgroundResource(mHotel.getCategory().getColorResId());

			name.setTypeface(DailyHotel.getBoldTypeface());
			discount.setTypeface(DailyHotel.getBoldTypeface());

			if (mRefreshingInfoWindow == false)
			{
				// Used Volley
				//				VolleyImageLoader.getImageLoader().get(mHotel.getImage(), new ImageListener()
				//				{
				//					
				//					@Override
				//					public void onErrorResponse(VolleyError arg0)
				//					{
				//						// TODO Auto-generated method stub
				//						
				//					}
				//					
				//					@Override
				//					public void onResponse(ImageContainer arg0, boolean arg1)
				//					{
				//						if(arg0 != null)
				//						{
				//							Bitmap bitmap = arg0.getBitmap();
				//							
				//							if(bitmap != null)
				//							{
				//								if(mRefreshingInfoWindow == false)
				//								{
				//									mRefreshingInfoWindow = true;
				//									img.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
				//									marker.showInfoWindow();
				//								}
				//							}
				//						}
				//					}
				//				});

				// Used AQuery
				AQuery aq = new AQuery(mView);
				Bitmap cachedImg = VolleyImageLoader.getCache(mHotel.getImage());

				if (cachedImg == null)
				{ // 힛인 밸류가 없다면 이미지를 불러온 후 캐시에 세이브
					BitmapAjaxCallback cb = new BitmapAjaxCallback()
					{
						@Override
						protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
						{
							VolleyImageLoader.putCache(url, bm);
							super.callback(url, iv, bm, status);

							mRefreshingInfoWindow = true;
							marker.showInfoWindow();
						}
					};

					cb.url(mHotel.getImage());
					aq.id(img).image(cb);

				} else
				{
					aq.id(img).image(cachedImg);
					//				cachedImg.recycle();

					mRefreshingInfoWindow = true;
					marker.showInfoWindow();
				}
			}

			// 객실이 1~2 개일때 label 표시
			int avail_cnt = mHotel.getAvailableRoom();

			// SOLD OUT 표시
			if (avail_cnt == 0)
			{
				sold_out.setVisibility(View.VISIBLE);
			} else
			{
				sold_out.setVisibility(View.GONE);
			}
		}

		return mView;
	}

	@Override
	public View getInfoContents(Marker marker)
	{
		return null;
	}
}
