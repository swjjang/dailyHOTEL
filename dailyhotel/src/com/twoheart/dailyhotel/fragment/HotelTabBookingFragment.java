package com.twoheart.dailyhotel.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.obj.HotelDetail;
import com.viewpagerindicator.CirclePageIndicator;

public class HotelTabBookingFragment extends Fragment implements
		OnTouchListener {

	private static final String TAG = "HotelTabBookingFragment";
	private static final int DURATION_HOTEL_IMAGE_SHOW = 2000;

	private HotelTabActivity mHostActivity;
	private HotelDetail mHotelDetail;

	private FragmentPagerAdapter mAdapter;
	private ViewPager mViewPager;
	private CirclePageIndicator mIndicator;

	private TextView tvBedType, tvAddress, tvPrice, tvDiscount;

	private Handler mHandler;
	private int mCurrentPage = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_tab_booking, container,
				false);
		mHostActivity = (HotelTabActivity) getActivity();
		mHotelDetail = mHostActivity.hotelDetail;

		tvBedType = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_bed_type);
		tvAddress = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_address);
		tvPrice = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price);
		tvDiscount = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_discount);
		mViewPager = (ViewPager) view
				.findViewById(R.id.vp_hotel_tab_booking_img);
		mIndicator = (CirclePageIndicator) view
				.findViewById(R.id.cp_hotel_tab_booking_indicator);

		tvBedType.setText(mHostActivity.hotelDetail.getHotel().getBedType());
		tvAddress.setText(mHostActivity.hotelDetail.getHotel().getAddress());
		tvAddress.setSelected(true);
		tvDiscount.setText(mHotelDetail.getHotel().getDiscount() + "¿ø");
		tvPrice.setText(mHotelDetail.getHotel().getPrice() + "¿ø");
		tvPrice.setPaintFlags(tvPrice.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);
		
		mAdapter = new FragmentPagerAdapter(this.getChildFragmentManager()) {

			@Override
			public Fragment getItem(int position) {
				return ImageViewFragment.newInstance(mHotelDetail.getImageUrl()
						.get(position), mHotelDetail);
			}

			@Override
			public int getCount() {
				return mHotelDetail.getImageUrl().size();
			}
			
		};
		
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnTouchListener(this);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setSnap(true);
		
		mHandler = new Handler() {
			public void handleMessage(Message msg) {

				mCurrentPage = mViewPager.getCurrentItem();
				mCurrentPage++;
				if (mCurrentPage == mHotelDetail.getImageUrl().size()) {
					mCurrentPage = 0;
				}
				mViewPager.setCurrentItem(mCurrentPage, true);
				this.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
			}
		};

		mHandler.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);

		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mHandler != null)
			mHandler.removeMessages(0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v.getId() == mViewPager.getId()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHandler.removeMessages(0);
				break;
			case MotionEvent.ACTION_MOVE:
				mHandler.removeMessages(0);
				break;

			case MotionEvent.ACTION_UP:
				mHandler.sendEmptyMessageDelayed(0,
						DURATION_HOTEL_IMAGE_SHOW * 2);
			default:
				break;
			}
		}

		return false;
	}
	
}
