package com.twoheart.dailyhotel.activity;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ImageDetailListActivity extends BaseActivity implements Constants
{
	private HotelDetail mHotelDetail;
	private ImageDetailListAdapter mAdapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_detail_list);

		int position = 0;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			mHotelDetail = (HotelDetail) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
			position = bundle.getInt(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION);
		}

		if (mHotelDetail == null)
		{
			finish();
			return;
		}

		mListView = (ListView) findViewById(R.id.listView);

		mAdapter = new ImageDetailListAdapter(this, 0, mHotelDetail.getImageUrl());
		mListView.setAdapter(mAdapter);

		mListView.setSelection(position);
	}

	private class ImageDetailListAdapter extends ArrayAdapter<String>
	{
		private AQuery mAquery;

		public ImageDetailListAdapter(Context context, int resourceId, List<String> list)
		{
			super(context, resourceId, list);

			mAquery = new AQuery(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;

			String url = getItem(position);

			if (convertView == null)
			{
				LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.list_row_image, parent, false);
			} else
			{
				view = convertView;
			}

			ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
			Bitmap cachedImg = VolleyImageLoader.getCache(url);

			if (cachedImg == null)
			{
				BitmapAjaxCallback cb = new BitmapAjaxCallback()
				{
					@Override
					protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
					{
						VolleyImageLoader.putCache(url, bm);

						setImageViewHeight(iv, bm);

						super.callback(url, iv, bm, status);
					}
				};

				cb.url(url).animation(AQuery.FADE_IN);
				mAquery.id(imageView).image(cb);
			} else
			{
				setImageViewHeight(imageView, cachedImg);

				imageView.setImageBitmap(cachedImg);
				//				mAquery.id(imageView).image(cachedImg).animate(R.anim.fade_in);
			}

			return view;
		}

		private void setImageViewHeight(ImageView imageView, Bitmap bitmap)
		{
			if (bitmap.getWidth() >= bitmap.getHeight())
			{
				RelativeLayout.LayoutParams layoutParms = (android.widget.RelativeLayout.LayoutParams) imageView.getLayoutParams();

				int height = Util.getLCDWidth(getContext());

				if (layoutParms == null)
				{
					layoutParms = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
				} else
				{
					layoutParms.height = height;
				}

				imageView.setLayoutParams(layoutParms);
			} else
			{
				RelativeLayout.LayoutParams layoutParms = (android.widget.RelativeLayout.LayoutParams) imageView.getLayoutParams();

				if (layoutParms == null)
				{
					layoutParms = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				} else
				{
					layoutParms.height = LayoutParams.WRAP_CONTENT;
				}

				imageView.setLayoutParams(layoutParms);
			}
		}
	}
}
