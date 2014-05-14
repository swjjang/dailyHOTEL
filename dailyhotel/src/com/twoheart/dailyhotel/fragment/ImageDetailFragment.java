package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class ImageDetailFragment extends BaseFragment {
	
	private static final String KEY_BUNDLE_ARGUMENTS_IMAGEURL = "image_url";
	
	private String mImageUrl;
//	private ImageLoader mImageLoader;
	private AQuery mAq;
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	
	public static ImageDetailFragment newInstance(String imageUrl) {
		ImageDetailFragment newFragment = new ImageDetailFragment();
		
		Bundle arguments = new Bundle();
		arguments.putString(KEY_BUNDLE_ARGUMENTS_IMAGEURL, imageUrl);
		newFragment.setArguments(arguments);
		
		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		mImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
//		mImageLoader = VolleyImageLoader.getImageLoader();
//		mImageLoader = new ImageLoader(mHostActivity);
		mImageUrl = getArguments().getString(KEY_BUNDLE_ARGUMENTS_IMAGEURL);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_detail, container,
				false);
		
		mImageView = (ImageView) view.findViewById(R.id.iv_image_detail);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_detail);

//		mImageView.setImageUrl(mImageUrl, mImageLoader, mProgressBar);
//		mImageLoader.displayImage(mImageUrl, mImageView, new ProgressImageLoading(mProgressBar));
//		mImageLoader.DisplayImage(mImageUrl, mImageView, mProgressBar);
		mAq = new AQuery(mHostActivity, view);
		mAq.id(mImageView).progress(mProgressBar).image(mImageUrl, true, true, 0, R.drawable.img_placeholder, null,
        		AQuery.FADE_IN);

		return view;
	}

}
