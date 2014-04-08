package com.twoheart.dailyhotel.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.ImageDetailFragment;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class ImageDetailActivity extends BaseActivity implements Constants {

	private static final String TAG = "ImageDetailActivity";
	
	private HotelDetail mHotelDetail;
	private FragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private CirclePageIndicator mIndicator;
	private String mSelectedImageUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarHide();
		setContentView(R.layout.activity_image_detail);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mHotelDetail = (HotelDetail) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
			mSelectedImageUrl = bundle.getString(NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL);
		}
		
		mPager = (ViewPager) findViewById(R.id.imagedetail_pager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.imagedetail_indicator);
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public Fragment getItem(int position) {
				return ImageDetailFragment.newInstance(mHotelDetail.getImageUrl().get(position));
			}
			
			@Override
			public int getCount() {
				return mHotelDetail.getImageUrl().size();
			}
			
		}; 
		
		mPager.setAdapter(mAdapter);
		mIndicator.setViewPager(mPager);
		mIndicator.setSnap(true);
		mPager.setCurrentItem(findSelectedImage(mSelectedImageUrl));
		
		
	}
	
	private int findSelectedImage(String imageUrl) {
		for (int i=0; i<mHotelDetail.getImageUrl().size(); i++) {
			if (mHotelDetail.getImageUrl().get(i).equals(imageUrl)) {
				return i;
			}
		}
		
		return 0;
	}
	
}
