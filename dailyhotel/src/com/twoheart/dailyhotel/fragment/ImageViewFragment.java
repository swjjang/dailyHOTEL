package com.twoheart.dailyhotel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.ImageDetailActivity;
import com.twoheart.dailyhotel.obj.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.widget.FadeInNetworkImageView;

public class ImageViewFragment extends Fragment implements OnClickListener, Constants {

	private HotelDetail mHotelDetail;
	private String mImageUrl;
	private ImageLoader mImageLoader;
	private FadeInNetworkImageView mImageView;
	private ProgressBar mProgressBar;
	private Context mContext;

	public ImageViewFragment(String imageUrl, Context context, HotelDetail hotelDetail) {
		mContext = context;
		
		VolleyImageLoader.init(mContext);
		mImageUrl = imageUrl;
		
		mHotelDetail = hotelDetail;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_view, container,
				false);
		mImageView = (FadeInNetworkImageView) view.findViewById(R.id.iv_image_view);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_view);
		mImageLoader = VolleyImageLoader.getImageLoader();
		mImageView.setImageUrl(mImageUrl, mImageLoader, mProgressBar);

		mImageView.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mImageView.getId()) {
			Intent i = new Intent(mContext, ImageDetailActivity.class);
			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, mHotelDetail);
			startActivity(i);
		}
	}
}
