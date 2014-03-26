package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.ImageDetailFragment;
import com.twoheart.dailyhotel.obj.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class ImageDetailActivity extends BaseActivity implements Constants {

	private static final String TAG = "ImageDetailActivity";
	
	private HotelDetail mHotelDetail;
	private FragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_detail);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mHotelDetail = (HotelDetail) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
		}
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public Fragment getItem(int position) {
				return new ImageDetailFragment(mHotelDetail.getImageUrl().get(position), ImageDetailActivity.this);
			}
			
			@Override
			public int getCount() {
				return mHotelDetail.getImageUrl().size();
			}
			
		}; 
		
		mPager = (ViewPager) findViewById(R.id.imagedetail_pager);
		mPager.setAdapter(mAdapter);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.imagedetail_indicator);
		this.mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setSnap(true);
		
	}
	
}
