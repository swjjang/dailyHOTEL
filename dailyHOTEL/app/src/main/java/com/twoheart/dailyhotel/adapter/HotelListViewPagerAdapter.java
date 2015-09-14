package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelListMapFragment;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.view.HotelListViewItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HotelListViewPagerAdapter extends PagerAdapter
{
	private Context mContext;
	private ArrayList<HotelListViewItem> mHotelListViewItemList;
	private HotelListMapFragment.OnUserActionListener mOnUserActionListener;

	public HotelListViewPagerAdapter(Context context)
	{
		mContext = context;

		mHotelListViewItemList = new ArrayList<HotelListViewItem>();
	}

	public void setOnUserActionListener(HotelListMapFragment.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		if (mHotelListViewItemList == null || mHotelListViewItemList.size() < position)
		{
			return null;
		}

		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.viewpager_column_hotel, null);

		HotelListViewItem item = mHotelListViewItemList.get(position);

		makeLayout(view, item.getItem());

		container.addView(view, 0);

		return view;
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	private void makeLayout(View view, final Hotel hotel)
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
		View dBenefitImageView = view.findViewById(R.id.dBenefitImageView);

		DecimalFormat comma = new DecimalFormat("###,##0");

		address.setText(hotel.getAddress());
		name.setText(hotel.getName());

		// D.benefit
		if (hotel.isDBenefit == true)
		{
			dBenefitImageView.setVisibility(View.VISIBLE);
		} else
		{
			dBenefitImageView.setVisibility(View.GONE);
		}

		Spanned currency = Html.fromHtml(mContext.getResources().getString(R.string.currency));

		int price = hotel.getPrice();

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

		View averageTextView = view.findViewById(R.id.averageTextView);

		if (hotel.nights > 1)
		{
			averageTextView.setVisibility(View.VISIBLE);
		} else
		{
			averageTextView.setVisibility(View.GONE);
		}

		discountTextView.setText(comma.format(hotel.averageDiscount) + currency);

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
		grade.setText(hotel.getCategory().getName(mContext));
		grade.setBackgroundResource(hotel.getCategory().getColorResId());

		// Used AQuery
		AQuery aquery = new AQuery(view);
		Bitmap cachedImg = VolleyImageLoader.getCache(hotel.getImage());

		if (cachedImg == null)
		{ // 힛인 밸류가 없다면 이미지를 불러온 후 캐시에 세이브
			BitmapAjaxCallback cb = new BitmapAjaxCallback()
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
				cb.url(hotel.getImage()).animation(AQuery.FADE_IN);
				aquery.id(img).image(hotel.getImage(), false, false, 240, 0, cb);
			} else
			{
				cb.url(hotel.getImage()).animation(AQuery.FADE_IN);
				aquery.id(img).image(cb);
			}
		} else
		{
			aquery.id(img).image(cachedImg);
		}

		// 객실이 1~2 개일때 label 표시
		int avail_cnt = hotel.getAvailableRoom();

		// SOLD OUT 표시
		if (avail_cnt == 0)
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
					mOnUserActionListener.onInfoWindowClickListener(hotel);
				}
			}
		});
	}

	@Override
	public int getCount()
	{
		if (mHotelListViewItemList != null)
		{
			return mHotelListViewItemList.size();
		} else
		{
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View) object);
	}

	public void setData(ArrayList<HotelListViewItem> list)
	{
		if (mHotelListViewItemList == null)
		{
			mHotelListViewItemList = new ArrayList<HotelListViewItem>();
		}

		mHotelListViewItemList.clear();

		if (list != null)
		{
			mHotelListViewItemList.addAll(list);
		}
	}
}
