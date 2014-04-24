package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
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
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.VolleyImageLoader;
import com.twoheart.dailyhotel.util.ui.ProgressImageLoading;
import com.twoheart.dailyhotel.widget.FadeInNetworkImageView;

public class ImageViewFragment extends Fragment implements OnClickListener, Constants {
	
	private static final String KEY_BUNDLE_ARGUMENTS_HOTELDETAIL = "hoteldetail";
	private static final String KEY_BUNDLE_ARGUMENTS_IMAGEURL = "image_url";

	private HotelDetail mHotelDetail;
	private String mImageUrl;
	private ImageLoader mImageLoader;
	private FadeInNetworkImageView mImageView;
	private ProgressBar mProgressBar;
	private Activity mHostActivity;
	
	public static ImageViewFragment newInstance(String imageUrl, HotelDetail hotelDetail) {
		
		ImageViewFragment newFragment = new ImageViewFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTELDETAIL, hotelDetail);
		arguments.putString(KEY_BUNDLE_ARGUMENTS_IMAGEURL, imageUrl);
		newFragment.setArguments(arguments);
		
		return newFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHostActivity = getActivity();
//		mImageLoader = ImageLoader.getInstance();
		mImageLoader = VolleyImageLoader.getImageLoader();
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTELDETAIL);
		mImageUrl = getArguments().getString(KEY_BUNDLE_ARGUMENTS_IMAGEURL);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_view, container,
				false);
//		mImageView = (ImageView) view.findViewById(R.id.iv_image_view);
		mImageView = (FadeInNetworkImageView) view.findViewById(R.id.iv_image_view);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_view);
		
		mImageView.setOnClickListener(this);
//		mImageLoader.displayImage(mImageUrl, mImageView, new ProgressImageLoading(mProgressBar));
		mImageView.setImageUrl(mImageUrl, mImageLoader, mProgressBar);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mImageView.getId()) {
			Intent i = new Intent(mHostActivity, ImageDetailActivity.class);
			i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, mHotelDetail);
			i.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL, mImageUrl);
			startActivity(i);
		}
	}

}
