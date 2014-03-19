package com.twoheart.dailyhotel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.obj.HotelDetail;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.widget.FadeInNetworkImageView;


public class ImageDetailFragment extends Fragment{
	
	private String mImageUrl;
	private ImageLoader mImageLoader;
	private FadeInNetworkImageView mImageView;
	private ProgressBar mProgressBar;
	private Context mContext;

	public ImageDetailFragment(String imageUrl, Context context) {
		mContext = context;
		
		VolleyImageLoader.init(mContext);
		mImageUrl = imageUrl;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_detail, container,
				false);
		mImageView = (FadeInNetworkImageView) view.findViewById(R.id.iv_image_detail);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_detail);
		mImageLoader = VolleyImageLoader.getImageLoader();
		mImageView.setImageUrl(mImageUrl, mImageLoader, mProgressBar);

		return view;
	}

}
