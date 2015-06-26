package com.twoheart.dailyhotel.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class HotelDetailImageViewPagerAdapter extends PagerAdapter
{
	private Context mContext;
	private List<String> mImageUrlList;
	private AQuery mAQuery;

	public HotelDetailImageViewPagerAdapter(Context context)
	{
		mContext = context;
	}

	public void setData(List<String> list)
	{
		mImageUrlList = list;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		if (mImageUrlList == null)
		{
			return null;
		}

		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ScaleType.CENTER_CROP);

		String url = mImageUrlList.get(position);

		mAQuery = new AQuery(mContext);
		mAQuery.id(imageView).image(url, true, true, 0, R.drawable.img_placeholder, null, AQuery.FADE_IN_NETWORK);

		int width = Util.getLCDWidth(mContext);

		LayoutParams layoutParams = new LayoutParams(width, width);
		container.addView(imageView, 0, layoutParams);

		return imageView;
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public int getCount()
	{
		if (mImageUrlList != null)
		{
			if (mImageUrlList.size() == 0)
			{
				return 1;
			} else
			{
				return mImageUrlList.size();
			}
		} else
		{
			return 1;
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
}
