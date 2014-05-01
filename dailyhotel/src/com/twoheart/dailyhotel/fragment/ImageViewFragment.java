package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.ImageDetailActivity;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class ImageViewFragment extends BaseFragment implements OnClickListener, Constants {
	
	private static final String KEY_BUNDLE_ARGUMENTS_HOTELDETAIL = "hoteldetail";
	private static final String KEY_BUNDLE_ARGUMENTS_IMAGEURL = "image_url";

	private HotelDetail mHotelDetail;
	private String mImageUrl;
//	private ImageLoader mImageLoader;
	private AQuery mAq;
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	
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
		
//		mImageLoader = ImageLoader.getInstance();
//		mImageLoader = VolleyImageLoader.getImageLoader();
//		mImageLoader = new ImageLoader(mHostActivity);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTELDETAIL);
		mImageUrl = getArguments().getString(KEY_BUNDLE_ARGUMENTS_IMAGEURL);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_image_view, container,
				false);
//		mImageView = (ImageView) view.findViewById(R.id.iv_image_view);
		mImageView = (ImageView) view.findViewById(R.id.iv_image_view);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_image_view);
		
		mImageView.setOnClickListener(this);
//		mImageLoader.displayImage(mImageUrl, mImageView, new ProgressImageLoading(mProgressBar));
//		mImageView.setImageUrl(mImageUrl, mImageLoader, mProgressBar);
//		mImageLoader.DisplayImage(mImageUrl, mImageView, mProgressBar);
		mAq = new AQuery(mHostActivity, view);
		mAq.id(mImageView).progress(mProgressBar).image(mImageUrl, true, true, 0, R.drawable.img_placeholder, null,
        		AQuery.FADE_IN_NETWORK);
		
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
