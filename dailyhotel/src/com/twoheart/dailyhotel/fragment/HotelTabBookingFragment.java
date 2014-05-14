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

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.HotelViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class HotelTabBookingFragment extends BaseFragment implements
		OnTouchListener {

	private static final String TAG = "HotelTabBookingFragment";
	private static final int DURATION_HOTEL_IMAGE_SHOW = 2000;
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

	private HotelDetail mHotelDetail;
	private FragmentPagerAdapter mAdapter;
	private HotelViewPager mViewPager;
	private CirclePageIndicator mIndicator;
	private TextView tvBedType, tvAddress, tvPrice, tvDiscount;

	private Handler mHandler;
	private int mCurrentPage = 0;
	
	public static HotelTabBookingFragment newInstance(HotelDetail hotelDetail) {
		
		HotelTabBookingFragment newFragment = new HotelTabBookingFragment();
		Bundle arguments = new Bundle();
		
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);
		newFragment.setTitle("예약");
		
		return newFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHotelDetail = (HotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_tab_booking, container,
				false);

		tvBedType = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_bed_type);
		tvAddress = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_address);
		tvPrice = (TextView) view.findViewById(R.id.tv_hotel_tab_booking_price);
		tvDiscount = (TextView) view
				.findViewById(R.id.tv_hotel_tab_booking_discount);
		mViewPager = (HotelViewPager) view
				.findViewById(R.id.vp_hotel_tab_booking_img);
		mIndicator = (CirclePageIndicator) view
				.findViewById(R.id.cp_hotel_tab_booking_indicator);

		tvBedType.setText(mHotelDetail.getHotel().getBedType());
		tvAddress.setText(mHotelDetail.getHotel().getAddress());
		tvAddress.setSelected(true);
		tvDiscount.setText(mHotelDetail.getHotel().getDiscount() + "원");
		tvPrice.setText(mHotelDetail.getHotel().getPrice() + "원");
		tvPrice.setPaintFlags(tvPrice.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);
		
		if (mAdapter == null) {
			mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

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
		} else {
			mAdapter.notifyDataSetChanged();
		}
		
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
	public void onResume(){
		super.onResume();
		tvDiscount.setTypeface(DailyHotel.getBoldTypeface());
		
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
