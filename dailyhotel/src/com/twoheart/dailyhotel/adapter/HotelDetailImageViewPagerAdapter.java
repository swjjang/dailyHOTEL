package com.twoheart.dailyhotel.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.ui.AnimationImageView;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;

public class HotelDetailImageViewPagerAdapter extends PagerAdapter
{
	private Context mContext;
	private List<String> mImageUrlList;
	private AQuery mAQuery;
	private int mDirection;

	private HotelDetailActivity.OnUserActionListener mOnUserActionListener;

	public HotelDetailImageViewPagerAdapter(Context context)
	{
		mContext = context;
	}

	public void setData(List<String> list)
	{
		mImageUrlList = list;
	}
	
	public void setDirection(int direction)
	{
		mDirection = direction;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		ExLog.d("instantiateItem : " + position);

		if (mImageUrlList == null)
		{
			return null;
		}

		ImageView imageView = null;

		int width = Util.getLCDWidth(mContext);

		if (Util.isOverAPI11() == true)
		{
			imageView = new AnimationImageView(mContext, width, width, mDirection < 0);
			((AnimationImageView) imageView).setOnAnimationListener(mOnUserActionListener);
		} else
		{
			imageView = new ImageView(mContext);
			imageView.setScaleType(ScaleType.CENTER_CROP);
		}

		imageView.setTag(position);

		String url = mImageUrlList.get(position);

		if (mAQuery == null)
		{
			mAQuery = new AQuery(mContext);
		}

		Bitmap cachedImg = VolleyImageLoader.getCache(url);

		if (cachedImg == null)
		{ // 힛인 밸류가 없다면 이미지를 불러온 후 캐시에 세이브
			BitmapAjaxCallback cb = new BitmapAjaxCallback()
			{
				@Override
				protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
				{
					if(bm != null)
					{
						VolleyImageLoader.putCache(url, bm);
					}
					
					iv.setImageBitmap(bm);
				}
			};

			cb.url(url).animation(AQuery.FADE_IN);
			mAQuery.id(imageView).image(cb);
		} else
		{
			imageView.setImageBitmap(cachedImg);
		}

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

	public void setOnAnimationListener(HotelDetailActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}
}
