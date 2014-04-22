package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.ProgressImageLoading;


public class ImageDetailFragment extends Fragment {
	
	private static final String KEY_BUNDLE_ARGUMENTS_IMAGEURL = "image_url";
	
	private String mImageUrl;
	private ImageLoader mImageLoader;
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	private Activity mHostActivity;
	
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
		
		mHostActivity = getActivity();
		mImageLoader = ImageLoader.getInstance();
		mImageUrl = getArguments().getString(KEY_BUNDLE_ARGUMENTS_IMAGEURL);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_detail, container,
				false);
		
		mImageView = (ImageView) view.findViewById(R.id.iv_image_detail);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_detail);

		mImageLoader.displayImage(mImageUrl, mImageView, new ProgressImageLoading(mProgressBar));

		return view;
	}

}
